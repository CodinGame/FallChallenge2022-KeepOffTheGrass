import { Sprite } from 'pixi.js'
import { HEIGHT, WIDTH } from '../core/constants.js'
import { bell, ease, easeIn, easeOut, elastic } from '../core/transitions.js'
import { fitAspectRatio, lerp, lerpPosition, unlerp, lerpAngle } from '../core/utils.js'
import { BACKGROUND, BASH_FRAMES, BORDER, CRACKS, DESTROY_FRAMES, HUD, HUD_COLOR_COMMON, MATTER_COLLECT, MULTIPLE_UNIT, NEUTRAL_TILES, PLAYER_TILES, POOF_FRAMES, RECYCLER, RECYCLER_RECYCLE_FRAMES, RECYCLER_SPAWN_FRAMES, RECYCLING_TILE_FRAMES, SPEECH_HEIGHT, SPEECH_OFFSET_X, SPEECH_PAD_X, SPEECH_PAD_Y, SPEECH_WIDTH, SPEECH_Y, TILE_RATIOS, UNIT } from './assetConstants.js'
import { parseData, parseGlobalData } from './Deserializer.js'
import ev from './events.js'
import { TooltipManager } from './TooltipManager.js'
import { AnimData, CanvasInfo, CellDto, ContainerConsumer, CoordDto, Effect, EventDto, FrameData, FrameInfo, GlobalData, Mutations, PlayerInfo, RecyclerDto, RecyclerTile, SpeechBubble, Tile, UnitDto, UnitHistory } from './types.js'
import { angleDiff, fit, key, last, randomChoice, setAnimationProgress } from './utils.js'

const TILE_SIZE = 32
const HUD_HEIGHT = 140
const TEXT_COLOURS = [0x00ffff, 0xee786e]

interface EffectPool {
  [key: string]: Effect[]
}

const api = {
  options: {
    showSFX: true,
    debugMode: false,
    cellHistory: true,
    fightTokens: true
  },
  setCellHistory: () => {},
  setFightTokens: () => {},
  setDebug: () => {}
}
export { api }

const sameCoord = (a: CoordDto, b: CoordDto) => a.x === b.x && a.y === b.y
const extractUnitByCoord = (army: UnitDto[], coord: CoordDto): UnitDto|null => {
  const idx = army.findIndex(u => sameCoord(u.coord, coord))
  if (idx === -1) {
    return null
  }
  return army.splice(idx, 1)[0]
}
const coordToIdx = ({ x, y }, { height }) => x * height + y

export class ViewModule {
  states: FrameData[]
  globalData: GlobalData
  pool: EffectPool
  playerSpeed: number
  previousData: FrameData
  currentData: FrameData
  progress: number
  oversampling: number
  container: PIXI.Container
  time: number
  tileMap: Record<string, Tile>
  tiles: Tile[]
  unitLayer: PIXI.Container
  fxLayer: PIXI.Container
  tooltipManager: TooltipManager
  currentPartialData: {
    recyclers: RecyclerDto[]
    cells: CellDto[]
    units: UnitDto[][]
  }

  huds: {
    avatar: PIXI.Sprite
    matter: PIXI.BitmapText
    score: PIXI.BitmapText
    nickname: PIXI.BitmapText
  }[]

  bubbles: SpeechBubble[]

  constructor () {
    this.states = []
    this.pool = {}
    this.tooltipManager = new TooltipManager()
    this.time = 0

    window.debug = this
  }

  static get moduleName () {
    return 'graphics'
  }

  // Effects
  getFromPool (type: string): Effect {
    if (!this.pool[type]) {
      this.pool[type] = []
    }

    for (const e of this.pool[type]) {
      if (!e.busy) {
        e.busy = true
        e.display.visible = true
        return e
      }
    }

    const e = this.createEffect(type)
    this.pool[type].push(e)
    e.busy = true
    return e
  }

  createEffect (type: string): Effect {
    const UNIT_SIZE_COEFF = 0.84
    let display = null
    if (type === 'unit') {
      display = new PIXI.Container()
      const sprite = PIXI.Sprite.from(UNIT[0])
      const text = new PIXI.Text('1', {
        fontSize: '48px',
        fontFamily: 'Lato',
        fontWeight: 'bold',
        fill: 'white',
        strokeThickness: 2,
        stroke: 'black'
      })
      text.scale.set(0.5)
      display.addChild(sprite)
      display.addChild(text)
      sprite.anchor.set(0.49, 0.5)
      sprite.width = TILE_SIZE * UNIT_SIZE_COEFF
      sprite.height = TILE_SIZE * UNIT_SIZE_COEFF
      text.x -= TILE_SIZE / 2
      text.y -= TILE_SIZE / 2
      this.unitLayer.addChild(display)
    } else if (type === 'multiple-unit') {
      display = new PIXI.Container()
      const sprite = PIXI.Sprite.from(MULTIPLE_UNIT)
      sprite.width = TILE_SIZE
      sprite.height = TILE_SIZE
      const texts = []
      const textContainer = new PIXI.Container()
      for (let pIdx = 0; pIdx < 2; ++pIdx) {
        const text = new PIXI.Text('1', {
          fontSize: '48px',
          fontFamily: 'Lato',
          fontWeight: 'bold',
          fill: 'white',
          strokeThickness: 2,
          stroke: 'black'
        })
        text.tint = TEXT_COLOURS[pIdx]
        text.scale.set(0.5)
        texts.push(text)
        textContainer.addChild(text)
        text.x -= TILE_SIZE / 2
        text.y = pIdx === 0 ? -3 * TILE_SIZE / 4 : -TILE_SIZE / 4
      }
      display.addChild(sprite)
      display.addChild(textContainer)
      sprite.anchor.set(0.5)
      this.unitLayer.addChild(display)
    } else if (type === 'recycler') {
      display = new PIXI.Container()
      const sprite = PIXI.Sprite.from(RECYCLER[0])
      sprite.anchor.set(0.5)
      sprite.width = TILE_SIZE * 3.2
      sprite.height = TILE_SIZE * 3.2
      display.addChild(sprite)
      this.unitLayer.addChild(display)
    } else if (type === 'bash') {
      display = PIXI.AnimatedSprite.fromFrames(BASH_FRAMES)
      display.zIndex = 1000
      display.animationSpeed = 0
      display.anchor.set(0.5)
      display.width = TILE_SIZE * 1.9
      display.height = TILE_SIZE * 1.9
      this.unitLayer.addChild(display)
    } else if (type === 'destroy') {
      display = PIXI.AnimatedSprite.fromFrames(DESTROY_FRAMES)
      display.zIndex = 1000
      display.animationSpeed = 0
      display.anchor.set(0.5)
      display.width = TILE_SIZE * 4.5
      display.height = TILE_SIZE * 4.5
      this.unitLayer.addChild(display)
    } else if (type === 'matter-collect') {
      display = new PIXI.Container()
      const sprite = PIXI.Sprite.from(MATTER_COLLECT)
      sprite.scale.set(0.26)
      sprite.anchor.set(0, 0.5)
      const text = new PIXI.Text('+2', {
        fontSize: '48px',
        fontFamily: 'Lato',
        fontWeight: 'bold',
        fill: 'white',
        strokeThickness: 4,
        stroke: 'black',
        align: 'left'
      })
      text.anchor.set(1, 0.55)
      text.x -= TILE_SIZE * 0.05
      text.scale.set(20 / 48)

      display.addChild(sprite)
      display.addChild(text)
      this.unitLayer.addChild(display)
    } else if (type === 'poof') {
      const fx = PIXI.AnimatedSprite.fromFrames(POOF_FRAMES)
      fx.animationSpeed = 0
      fx.anchor.set(0.515, 0.495)
      fx.width = TILE_SIZE * 2.3
      fx.height = TILE_SIZE * 2.3
      display = fx
      this.unitLayer.addChild(display)
    } else if (type.startsWith('recycler_spawn_')) {
      const pIdx = parseInt(type.split('_')[2])
      display = new PIXI.Container()
      const spawn = PIXI.AnimatedSprite.fromFrames(RECYCLER_SPAWN_FRAMES[pIdx])
      spawn.anchor.set(0.5)
      spawn.width = TILE_SIZE * 3.2
      spawn.height = TILE_SIZE * 3.2
      display.addChild(spawn)
      spawn.animationSpeed = 0
      this.unitLayer.addChild(display)
    } else if (type.startsWith('recycler_recycle_')) {
      const pIdx = parseInt(type.split('_')[2])
      display = new PIXI.Container()
      const fx = PIXI.AnimatedSprite.fromFrames(RECYCLER_RECYCLE_FRAMES[pIdx])
      fx.anchor.set(0.5)
      fx.width = TILE_SIZE
      fx.height = TILE_SIZE
      display.addChild(fx)
      display.zIndex = -2
      fx.animationSpeed = 0.33
      fx.gotoAndPlay(Math.random() * RECYCLING_TILE_FRAMES.length)
      this.unitLayer.addChild(display)
    }
    return { busy: false, display }
  }

  updateScene (previousData: FrameData, currentData: FrameData, progress: number, playerSpeed?: number) {
    const frameChange = (this.currentData !== currentData)
    const fullProgressChange = ((this.progress === 1) !== (progress === 1))

    this.previousData = previousData
    this.currentData = currentData
    this.progress = progress
    this.playerSpeed = playerSpeed || 0

    this.resetEffects()
    this.updateGrid(previousData, currentData, progress)
    this.updateUnits(previousData, currentData, progress)
    this.updateRecyclers(previousData, currentData, progress)
    this.updateHud(previousData, currentData, progress)
    if (frameChange || (fullProgressChange && playerSpeed === 0)) {
      this.tooltipManager.updateGlobalText()
    }
  }

  updateHud (previousData: FrameData, currentData: FrameData, progress: number) {
    const scores = [0, 0]

    const displayData = progress < 1 ? previousData : currentData

    for (const cell of displayData.cells) {
      if (cell.ownerIdx >= 0 && cell.durability > 0) {
        scores[cell.ownerIdx] += 1
      }
    }

    for (const player of this.globalData.players) {
      const hud = this.huds[player.index]
      hud.matter.text = `${displayData.players[player.index].money}`
      hud.score.text = `${scores[player.index]}`

      const message = currentData.players[player.index].message
      const speech = this.bubbles[player.index].speech

      this.bubbles[player.index].show = !!message
      const textMaxWidth = SPEECH_WIDTH - SPEECH_PAD_X
      const textMaxHeight = SPEECH_HEIGHT - SPEECH_PAD_Y

      speech.scale.set(1)
      speech.style.wordWrapWidth = textMaxWidth
      if (message) {
        speech.text = message
      }
      if (speech.height > textMaxHeight) {
        const scale = fitAspectRatio(speech.width, speech.height, textMaxWidth, textMaxHeight)
        speech.scale.set(scale)
        speech.style.wordWrapWidth *= 1 / speech.scale.x
      } else {
        const scale = fitAspectRatio(speech.width, speech.height, textMaxWidth, textMaxHeight)
        speech.scale.set(Math.min(1.6, scale))
      }
    }
  }

  getCurrentStay (history: UnitHistory[], progress: number): Omit<UnitHistory, 'p'> {
    if (history.length === 0) {
      return { n: 0 }
    }
    const idx = history.findIndex(h => h.p > progress)
    if (idx === -1) {
      return { n: last(history).n, angle: last(history).angle }
    }
    return {
      n: history[idx - 1]?.n ?? 0,
      angle: history[idx - 1]?.angle
    }
  }

  updateUnits (previousData: FrameData, currentData: FrameData, progress: number) {
    for (let pIdx = 0; pIdx < this.globalData.playerCount; ++pIdx) {
      const units = currentData.units[pIdx]
      for (const unit of units) {
        const stayed = this.getCurrentStay(unit.stayed, progress)
        let foeStayed: Omit<UnitHistory, 'p'>
        if (stayed.n > 0) {
          let multipleStaying = false
          if (unit.foe != null) {
            foeStayed = this.getCurrentStay(unit.foe.stayed, progress)
            if (foeStayed.n > 0) {
              multipleStaying = true
            }
          }

          if (multipleStaying && pIdx > 0) {
            continue
          }
          if (multipleStaying) {
            const unitEffect = this.getFromPool('multiple-unit')
            const unitDisplay = unitEffect.display as PIXI.Container
            const textContainer = unitDisplay.children[1] as PIXI.Container
            const texts = textContainer.children as PIXI.Text[]

            texts[0].text = `${stayed.n}`
            texts[1].text = `${foeStayed.n}`
            unitDisplay.zIndex = -1
            this.placeInGameZone(unitDisplay, unit.coord.x, unit.coord.y)
          } else {
            const cellIdx = coordToIdx(unit.coord, this.globalData)
            const cell = previousData.cells[cellIdx]
            const angle = stayed.angle ?? cell.lastAngle[pIdx] ?? Math.PI / 2
            const currentSpawnEvent = currentData.events.find(
              e => e.type === ev.SPAWN &&
              sameCoord(e.coord, unit.coord) &&
              unlerp(e.animData.start, e.animData.end, progress) > 0.5 &&
              unlerp(e.animData.start, e.animData.end, progress) < 1
            )

            this.makeUnit({
              pIdx,
              amount: currentSpawnEvent == null ? stayed.n : '',
              pos: unit.coord,
              rotation: angle
            })
          }
        }
      }
    }

    for (const e of currentData.events) {
      if ([ev.MOVE, ev.SPAWN, ev.UNIT_FALL].includes(e.type)) {
        const unit = this.currentData.units[e.playerIndex].find(u => sameCoord(u.coord, e.coord))
        const stayed = this.getCurrentStay(unit.stayed, progress)

        const cellIdx = coordToIdx(e.coord, this.globalData)
        const cell = this.previousData.cells[cellIdx]
        const angle = stayed.angle ?? cell.lastAngle[e.playerIndex] ?? Math.PI / 2

        if (e.type === ev.MOVE) {
          this.animateMove(e, currentData, progress, angle)
        } else if (e.type === ev.SPAWN) {
          this.animateSpawn(e, currentData, progress, angle, stayed.n)
        } else if (e.type === ev.UNIT_FALL) {
          this.animateFall(e, currentData, progress, angle)
        }
      }
    }
  }

  makeUnit (
    { scale = 1, zIndex = 0, alpha = 1, pIdx, amount, pos, rotation = Math.PI / 2 }:
    { scale?: number, zIndex?: number, alpha?: number, pIdx: number, amount: number|string, pos: CoordDto, rotation: number}
  ): PIXI.Container {
    const unitEffect = this.getFromPool('unit')
    const unitDisplay = unitEffect.display as PIXI.Container
    const sprite = unitDisplay.children[0] as PIXI.Sprite
    const text = unitDisplay.children[1] as PIXI.Text
    unitDisplay.scale.set(scale)
    unitDisplay.zIndex = zIndex
    unitDisplay.alpha = alpha
    sprite.texture = PIXI.Texture.from(UNIT[pIdx])
    text.text = `${amount}`
    this.placeInGameZone(unitDisplay, pos.x, pos.y)

    const targetRotation = rotation - Math.PI / 2
    sprite.rotation = targetRotation

    return unitDisplay
  }

  animateFall (event: EventDto, currentData: FrameData, progress: number, angle: number) {
    const p = this.getAnimProgress(event.animData, progress)
    if (p <= 0 || p >= 1) {
      return
    }

    const effect = this.getFromPool('destroy')
    const fx = effect.display as PIXI.AnimatedSprite
    setAnimationProgress(fx, p)
    this.placeInGameZone(fx, event.coord.x, event.coord.y)

    this.makeUnit({
      alpha: p < 0.3 ? 1 : 0,
      pIdx: event.playerIndex,
      amount: event.amount,
      pos: event.coord,
      rotation: angle
    })
  }

  animateMove (event: EventDto, currentData: FrameData, progress: number, angle: number) {
    const p = this.getAnimProgress(event.animData, progress)
    if (p <= 0 || p >= 1) {
      return
    }
    const toAngle = Math.atan2(event.target.y - event.coord.y, event.target.x - event.coord.x)

    this.makeUnit({
      pIdx: event.playerIndex,
      amount: event.amount,
      pos: lerpPosition(event.coord, event.target, ease(p)),
      rotation: lerpAngle(angle, toAngle, easeOut(p))
    })
  }

  isInFight (coord: CoordDto) {
    return this.currentData.cells[coordToIdx(coord, this.globalData)].fight
  }

  animateSpawn (event: EventDto, currentData: FrameData, progress: number, angle: number, n: number) {
    const p = this.getAnimProgress(event.animData, progress)
    if (p <= 0 || p >= 1) {
      return
    }
    const dropP = unlerp(0, 0.5, p)
    const poofP = unlerp(0.5, 1, p)
    const bounceP = unlerp(0.5, 0.75, p)

    const fx = this.getFromPool('poof').display as PIXI.AnimatedSprite
    setAnimationProgress(fx, poofP)
    this.placeInGameZone(fx, event.coord.x, event.coord.y)

    this.makeUnit({
      scale: dropP < 1 ? lerp(4, 0.8, dropP) : lerp(1, 1.1, bell(bounceP)),
      zIndex: lerp(50, 0, dropP),
      alpha: lerp(0, 1, dropP),
      pIdx: event.playerIndex,
      amount: p > 0.5 ? (n + event.amount) : event.amount,
      pos: event.coord,
      rotation: angle
    })
  }

  makeRecycler ({ scale = 1, zIndex = 1, pIdx, coord, alpha = 1 }) {
    const unitEffect = this.getFromPool('recycler')
    const unitDisplay = unitEffect.display as PIXI.Container
    const sprite = unitDisplay.children[0] as PIXI.Sprite
    unitDisplay.scale.set(scale)
    unitDisplay.zIndex = zIndex
    sprite.texture = PIXI.Texture.from(RECYCLER_SPAWN_FRAMES[pIdx][40])
    unitDisplay.alpha = alpha
    this.placeInGameZone(unitDisplay, coord.x, coord.y)
  }

  updateRecyclers (previousData: FrameData, currentData: FrameData, progress: number) {
    for (const { ownerIdx, coord, spawnAt, unspawnAt, events } of currentData.recyclerTiles) {
      let displayed = false
      for (const e of events) {
        if (e.type === ev.BUILD) {
          displayed = displayed || this.animateBuild(e, currentData, progress)
        } else if (e.type === ev.RECYCLER_FALL) {
          displayed = displayed || this.animateRecyclerFall(e, currentData, progress)
        } else if (e.type === ev.MATTER_COLLECT) {
          displayed = displayed || this.animateGainMatter(e, progress)
        }
      }
      if (
        !displayed &&
        (spawnAt == null || progress > spawnAt) &&
        (unspawnAt == null || progress < unspawnAt)
      ) {
        this.makeRecycler({
          pIdx: ownerIdx,
          coord: coord
        })
      }
    }
  }

  animateRecyclerFall (event: EventDto, currentData: FrameData, progress: number): boolean {
    const p = this.getAnimProgress(event.animData, progress)
    if (p <= 0 || p >= 1) {
      return false
    }

    const effect = this.getFromPool('destroy')
    const fx = effect.display as PIXI.AnimatedSprite
    setAnimationProgress(fx, p)
    this.placeInGameZone(fx, event.coord.x, event.coord.y)

    this.makeRecycler({
      pIdx: event.playerIndex,
      coord: event.coord,
      alpha: p < 0.3 ? 1 : 0
    })
    return true
  }

  animateBuild (event: EventDto, currentData: FrameData, progress: number): boolean {
    const p = this.getAnimProgress(event.animData, progress)
    if (p <= 0 || p >= 1) {
      return false
    }

    const display = this.getFromPool(`recycler_spawn_${event.playerIndex}`).display as PIXI.Container
    const spawn = display.children[0] as PIXI.AnimatedSprite
    setAnimationProgress(spawn, p)
    display.zIndex = lerp(100, 1, p)
    this.placeInGameZone(display, event.coord.x, event.coord.y)
    return true
  }

  placeInGameZone (display: PIXI.DisplayObject, x: number, y: number) {
    display.x = TILE_SIZE * x + TILE_SIZE / 2
    display.y = TILE_SIZE * y + TILE_SIZE / 2
  }

  shake (entity: PIXI.DisplayObject, progress: number) {
    const shakeForceMax = 1.4
    const omega = 100000 * (Math.random() * 0.5 + 0.5)

    const shakeForce = shakeForceMax * unlerp(0, 0.5, bell(progress))
    const shakeX = shakeForce * Math.cos(2 * progress * omega)
    const shakeY = shakeForce * Math.sin(progress * omega)

    entity.pivot.x = shakeX
    entity.pivot.y = shakeY
  }

  animateGainMatter (event: EventDto, progress: number): boolean {
    const p = this.getAnimProgress(event.animData, progress)
    if (p <= 0 || p >= 1) {
      return false
    }
    this.makeMatter({
      scale: 1,
      alpha: lerp(0, 2, bell(p)),
      pIdx: event.playerIndex,
      amount: event.amount,
      pos: { x: event.coord.x, y: event.coord.y - easeOut(p) }
    })

    const display = this.getFromPool(`recycler_recycle_${event.playerIndex}`).display as PIXI.Container
    const spawn = display.children[0] as PIXI.AnimatedSprite
    spawn.animationSpeed = lerp(1, 0.33, easeOut(p))
    display.alpha = lerp(1, 0, unlerp(0.8, 1, p))
    this.placeInGameZone(display, event.coord.x, event.coord.y)

    this.makeRecycler({
      pIdx: event.playerIndex,
      coord: event.coord,
      alpha: lerp(0, 1, unlerp(0.8, 1, p))
    })
    return true
  }

  makeMatter ({ scale = 1, alpha = 1, pIdx = 0, amount, pos }): PIXI.Container {
    const matterEffect = this.getFromPool('matter-collect')
    const matterDisplay = matterEffect.display as PIXI.Container
    const text = matterDisplay.children[1] as PIXI.Text
    matterDisplay.scale.set(scale)
    matterDisplay.zIndex = 100
    matterDisplay.alpha = alpha
    text.tint = TEXT_COLOURS[pIdx]
    text.text = `+${amount}`
    this.placeInGameZone(matterDisplay, pos.x, pos.y)
    return matterDisplay
  }

  updateGrid (previousData: FrameData, currentData: FrameData, progress: number) {
    for (let cellIdx = 0; cellIdx < currentData.cells.length; ++cellIdx) {
      const cell = currentData.cells[cellIdx]
      let recycleEvent = null
      let swapEvent = null
      let fightEvent = null
      for (const e of currentData.events) {
        if (sameCoord(e.coord, cell)) {
          if (e.type === ev.CELL_DAMAGE) {
            recycleEvent = e
          } else if (e.type === ev.CELL_OWNER_SWAP) {
            swapEvent = e
          } else if (e.type === ev.FIGHT) {
            fightEvent = e
          }
        }
      }

      const tile = this.tileMap[key(cell.x, cell.y)]
      this.drawTile(tile, cell)

      if (recycleEvent != null) {
        this.animateRecycle(tile, cell, recycleEvent, progress)
      }

      if (swapEvent != null) {
        const prevCell = currentData.previous.cells[cellIdx]
        const p = this.getAnimProgress(swapEvent.animData, progress)
        if (p < 1) {
          const tileIdx = this.getTileIdx(cell)
          this.animateSwap(tile, prevCell.ownerIdx, cell.ownerIdx, p, tileIdx)
        }
      }

      if (fightEvent != null) {
        this.animateFight(cell, fightEvent, progress)
      }
    }
  }

  animateRecycle (tile, cell, recycleEvent, progress) {
    const p = this.getAnimProgress(recycleEvent.animData, progress)
    tile.sprite.visible = true
    tile.sprite.alpha = cell.durability === 0 ? 1 - p : 1

    tile.cracks[0].visible = cell.durability >= 4 && cell.durability <= 6
    tile.cracks[1].visible = cell.durability >= 1 && cell.durability <= 4
    tile.cracks[2].visible = cell.durability <= 1

    if (cell.durability === 6) {
      tile.cracks[0].alpha = p
    }
    if (cell.durability === 4) {
      tile.cracks[1].alpha = p
      tile.cracks[0].alpha = 1 - p
    }
    if (cell.durability === 1) {
      tile.cracks[2].alpha = p
      tile.cracks[1].alpha = 1 - p
    }
    if (cell.durability === 0) {
      tile.cracks[2].alpha = 1 - p
      tile.border.alpha = 1 - p
    }

    if (p > 0 && p < 1) {
      this.shake(tile.sprite, p)

      tile.recycleFx.alpha = lerp(0, 1, bell(p))
      tile.recycleFx.play()
      tile.recycleFx.visible = true
    }
  }

  drawTile (tile: Tile, cell: CellDto) {
    tile.recycleFx.visible = false
    tile.overlay.visible = false
    tile.border.visible = (cell.durability > 0)
    tile.border.alpha = 1
    tile.sprite.texture = this.getTileTextureByOwnerIdx(cell.ownerIdx, this.getTileIdx(cell))
    tile.sprite.alpha = 1
    tile.sprite.visible = cell.durability > 0
    tile.sprite.pivot.set(0)
    tile.cracks[0].visible = cell.durability >= 5 && cell.durability <= 6
    tile.cracks[1].visible = cell.durability >= 2 && cell.durability <= 4
    tile.cracks[2].visible = cell.durability === 1
    tile.recycleFx.stop()
    tile.recycleFx.visible = false
  }

  getTileIdx (cell: CellDto) {
    return randomChoice(cell.rand, TILE_RATIOS)
  }

  animateFight (cell: CellDto, fightEvent: EventDto, progress: number) {
    const p = this.getAnimProgress(fightEvent.animData, progress)
    if (p <= 0 || p >= 1) {
      return
    }
    const effect = this.getFromPool('bash')
    const fx = effect.display as PIXI.AnimatedSprite
    setAnimationProgress(fx, p)
    this.placeInGameZone(fx, cell.x, cell.y)
  }

  animateSwap (tile: Tile, prevOwnerIdx: number, ownerIdx: number, p: number, tileIdx: number) {
    tile.sprite.texture = this.getTileTextureByOwnerIdx(prevOwnerIdx, tileIdx)
    tile.overlay.texture = this.getTileTextureByOwnerIdx(ownerIdx, tileIdx)
    tile.overlay.alpha = p
    tile.overlay.visible = true
  }

  getTileTextureByOwnerIdx (ownerIdx: number, tileIdx: number): PIXI.Texture {
    if (ownerIdx === -1) {
      return PIXI.Texture.from(NEUTRAL_TILES[tileIdx])
    } else {
      return PIXI.Texture.from(PLAYER_TILES[ownerIdx][tileIdx])
    }
  }

  toGlobal (element: PIXI.DisplayObject) {
    return this.container.toLocal(new PIXI.Point(0, 0), element)
  }

  getAnimProgress ({ start, end }: AnimData, progress: number) {
    return unlerp(start, end, progress)
  }

  upThenDown (t) {
    return Math.min(1, bell(t) * 10)
  }

  resetEffects () {
    for (const type in this.pool) {
      for (const effect of this.pool[type]) {
        effect.display.visible = false
        effect.busy = false
      }
    }
  }

  animateRotation (sprite: PIXI.Sprite, rotation: number) {
    if (sprite.rotation !== rotation) {
      const eps = 0.02
      let r = lerpAngle(sprite.rotation, rotation, 0.133)
      if (angleDiff(r, rotation) < eps) {
        r = rotation
      }
      sprite.rotation = r
    }
  }

  animateScene (delta) {
    this.time += delta

    for (let idx = 0; idx < this.globalData.playerCount; ++idx) {
      const { show, container } = this.bubbles[idx]
      const stepFactor = Math.pow(0.993 + (0.007 * (this.playerSpeed || 1) / 10), delta)
      const targetAlpha = show ? 1 : 0

      if (targetAlpha === 1) {
        container.alpha = 1
      } else {
        container.alpha = container.alpha * stepFactor + targetAlpha * (1 - stepFactor)
      }
    }
  }

  asLayer (func: ContainerConsumer): PIXI.Container {
    const layer = new PIXI.Container()
    func.bind(this)(layer)
    return layer
  }

  initBackground (layer: PIXI.Container) {
    const b = PIXI.Sprite.from(BACKGROUND)
    fit(b, Infinity, HEIGHT)
    layer.addChild(b)
  }

  initSpeechBubbles (layer) {
    this.bubbles = []
    for (let idx = 0; idx < this.globalData.playerCount; ++idx) {
      const player = this.globalData.players[idx]
      const flip = idx === 0 ? 1 : -1

      const container = new PIXI.Container()
      const speech = new PIXI.Text('', {
        fontSize: '40px',
        fontFamily: 'Arial',
        fontWeight: 'bold',
        align: 'center',
        fill: 'white',
        wordWrap: true,
        wordWrapWidth: SPEECH_WIDTH,
        lineHeight: 38
      })
      speech.position.set(
        (idx === 0 ? SPEECH_WIDTH / 2 : -SPEECH_WIDTH / 2),
        72 / 2
      )
      speech.anchor.set(0.5)

      container.position.set(WIDTH * idx + flip * SPEECH_OFFSET_X, SPEECH_Y)
      container.alpha = 0

      container.addChild(speech)
      layer.addChild(container)
      this.bubbles.push({ container, speech, show: false })
    }
  }

  initHud (layer: PIXI.Container) {
    this.huds = []
    const topBar = PIXI.Sprite.from(HUD)
    layer.addChild(topBar)

    for (const player of this.globalData.players) {
      const place = (x) => player.index === 0 ? x : WIDTH - x

      const backdrop = new PIXI.Sprite(PIXI.Texture.WHITE)
      backdrop.tint = 0x454142
      backdrop.position.set(place(50), 50)
      backdrop.width = 100
      backdrop.height = 100
      backdrop.anchor.set(0.5)

      const avatar = new PIXI.Sprite(player.avatar)
      avatar.position.set(place(51), 51)
      avatar.width = 96
      avatar.height = 96
      avatar.anchor.set(0.5)

      const MATTER_RECT = {
        x: 512,
        y: 10,
        w: 45,
        h: 40
      }
      const matter = new PIXI.BitmapText('10', {
        fontName: 'Lato',
        fontSize: 54,
        tint: HUD_COLOR_COMMON
      })
      fitTextWithin(matter, MATTER_RECT, place)

      const NICKNAME_RECT = {
        x: 123,
        y: 6,
        w: 290,
        h: 47
      }
      const nickname = new PIXI.BitmapText(player.name, {
        fontSize: 54,
        fontName: 'Lato'
      })
      fitTextWithin(nickname, NICKNAME_RECT, place)

      const SCORE_RECT = {
        x: 631,
        y: 20,
        w: 94,
        h: 66
      }
      const score = new PIXI.BitmapText('000', {
        fontSize: 64,
        fontName: 'Lato'
      })
      fitTextWithin(score, SCORE_RECT, place)

      this.huds.push({
        avatar,
        matter,
        score,
        nickname
      })

      const playerHud = new PIXI.Container()

      playerHud.addChild(backdrop)
      playerHud.addChild(avatar)
      playerHud.addChild(matter)
      playerHud.addChild(score)
      playerHud.addChild(nickname)
      layer.addChild(playerHud)
    }
  }

  initGrid (layer: PIXI.Container) {
    this.tiles = []
    this.tileMap = {}

    const fx = new PIXI.Container()
    const map = new PIXI.Container()
    for (let y = 0; y < this.globalData.height; ++y) {
      for (let x = 0; x < this.globalData.width; ++x) {
        const tileContainer = new PIXI.Container()
        tileContainer.x = TILE_SIZE * x
        tileContainer.y = TILE_SIZE * y

        const tileSprite = PIXI.Sprite.from(NEUTRAL_TILES[0])
        tileSprite.width = TILE_SIZE
        tileSprite.height = TILE_SIZE

        const overlay = PIXI.Sprite.from(NEUTRAL_TILES[0])
        overlay.width = TILE_SIZE
        overlay.height = TILE_SIZE

        tileContainer.addChild(tileSprite)
        tileContainer.addChild(overlay)

        const cracks = []
        for (let idx = 0; idx < CRACKS.length; ++idx) {
          const crack = PIXI.Sprite.from(CRACKS[idx])
          crack.width = TILE_SIZE
          crack.height = TILE_SIZE
          crack.alpha = 1
          crack.visible = false
          cracks.push(crack)
          tileContainer.addChild(crack)
        }

        const border = PIXI.Sprite.from(BORDER)
        border.width = TILE_SIZE
        border.height = TILE_SIZE
        tileContainer.addChild(border)

        const recycleFx = PIXI.AnimatedSprite.fromFrames(RECYCLING_TILE_FRAMES)
        recycleFx.anchor.set(0.5)
        recycleFx.width = TILE_SIZE * 1.64
        recycleFx.height = TILE_SIZE * 1.64
        recycleFx.animationSpeed = 0.33
        recycleFx.gotoAndStop(Math.random() * RECYCLING_TILE_FRAMES.length)
        recycleFx.visible = false
        recycleFx.x = TILE_SIZE * x + TILE_SIZE / 2
        recycleFx.y = TILE_SIZE * y + TILE_SIZE / 2
        fx.addChild(recycleFx)
        const tile: Tile = {
          sprite: tileSprite,
          overlay,
          baseScale: overlay.scale.x,
          cracks,
          border,
          recycleFx
        }
        this.tiles.push(tile)
        this.tileMap[key(x, y)] = tile

        map.addChild(tileContainer)
      }
    }

    layer.addChild(map)
    layer.addChild(fx)
  }

  reinitScene (container: PIXI.Container, canvasData: CanvasInfo) {
    this.oversampling = canvasData.oversampling
    this.container = container
    this.pool = {}

    const tooltipLayer = this.tooltipManager.reinit()
    const gameZone = new PIXI.Container()
    const gridLayer = this.asLayer(this.initGrid)
    this.unitLayer = new PIXI.Container()
    this.unitLayer.sortableChildren = true
    this.fxLayer = new PIXI.Container()
    gameZone.addChild(gridLayer)
    const hudLayer = this.asLayer(this.initHud)
    const background = this.asLayer(this.initBackground)
    const bubbleLayer = this.asLayer(this.initSpeechBubbles)

    const coeff = fitAspectRatio(gameZone.width, gameZone.height, WIDTH, HEIGHT - HUD_HEIGHT, 40)
    gameZone.scale.set(coeff)
    gameZone.x = WIDTH / 2 - gameZone.width / 2
    gameZone.y = HEIGHT / 2 - gameZone.height / 2 + HUD_HEIGHT / 2

    gameZone.addChild(this.unitLayer)

    container.addChild(background)
    container.addChild(gameZone)
    container.addChild(hudLayer)
    container.addChild(bubbleLayer)
    container.addChild(tooltipLayer)

    container.interactive = true
    tooltipLayer.interactiveChildren = false
    hudLayer.interactiveChildren = false

    container.on('mousemove', (event) => {
      this.tooltipManager.moveTooltip(event)
    })

    this.tooltipManager.registerGlobal((data: PIXI.InteractionData) => {
      const localPos = data.getLocalPosition(gridLayer)
      const tileX = Math.floor(localPos.x / TILE_SIZE)
      const tileY = Math.floor(localPos.y / TILE_SIZE)

      if (tileX < this.globalData.width && tileY < this.globalData.height && tileX >= 0 && tileY >= 0) {
        let text = `(${tileX}, ${tileY})`
        if (this.currentData != null) {
          const cIdx = coordToIdx({ x: tileX, y: tileY }, this.globalData)
          const cell = this.currentData.cells[cIdx]
          const hp = cell.durability

          if (this.progress !== 1 || this.playerSpeed > 0) {
            text += '\n---at turn end---'
          }
          text += `\nscrap amount: ${hp}`

          if (api.options.cellHistory && cell.history.length > 0) {
            text += `\n${cell.history.join('\n')}`
          }
        }
        return text
      }
      return null
    })
  }

  registerTooltip (container: PIXI.Container, getString: () => string) {
    container.interactive = true
    this.tooltipManager.register(container, getString)
  }

  frame (o, col = 0xFF00FF, ancX, ancY) {
    const frame = new PIXI.Graphics()
    const x = -o.width * (o.anchor?.x ?? ancX)
    const y = -o.height * (o.anchor?.y ?? ancY)
    frame.beginFill(col, 1)
    frame.drawRect(x, y, o.width, o.height)
    frame.position.copyFrom(o)
    return frame
  }

  handleGlobalData (players: PlayerInfo[], raw: string): void {
    const globalData = parseGlobalData(raw)

    this.globalData = {
      ...globalData,
      players: players,
      playerCount: players.length
    }
  }

  handleEvent (event: EventDto, { recycler, cell, unit: unitMutator }: Mutations, eventHistory?: string) {
    const { recyclers, cells, units } = this.currentPartialData

    const cellIdx = coordToIdx(event.coord, this.globalData)

    /* Recyclers */
    if (recycler != null) {
      const rIdx = recyclers.findIndex(r => sameCoord(r.coord, event.coord))
      const rec = rIdx > -1 ? recyclers[rIdx] : null
      const newRec = recycler()
      if (newRec != null) {
        recyclers.push(newRec)
      } else if (rIdx > -1) {
        recyclers.splice(rIdx, 1)
      }
    }
    /* Cells */
    if (cell != null) {
      cells[cellIdx] = {
        ...cells[cellIdx],
        ...cell(cells[cellIdx])
      }
    }
    if (eventHistory != null) {
      cells[cellIdx] = {
        ...cells[cellIdx],
        history: [...cells[cellIdx].history, eventHistory]
      }
    }

    /* Units */
    if (unitMutator != null) {
      const army = units[event.playerIndex]
      const unit = extractUnitByCoord(army, event.coord)

      const { strength, p, angle } = unitMutator(unit)
      const stayStep: UnitHistory = {
        p,
        n: strength,
        angle: angle ?? last(unit?.stayed ?? [])?.angle
      }

      army.push({
        ...(unit ?? {
          coord: event.coord,
          foe: null
        }),
        strength,
        stayed: [...(unit?.stayed ?? []), stayStep]
      })
    }
  }

  handleFrameData (frameInfo: FrameInfo, raw: string): FrameData {
    const colors = ['blue', 'red']
    const dto = parseData(raw, this.globalData)
    const source = last(this.states) ?? this.globalData
    const cells: CellDto[] = source.cells.map(c => ({ ...c, fight: false, history: [], lastAngle: { ...c.lastAngle } }))
    const units: UnitDto[][] = source.units.map(army => [
      ...(
        army
          .filter(u => u.strength > 0)
          .map(u => ({
            ...u,
            foe: null,
            stayed: u.strength > 0
              ? [{ n: u.strength, p: 0 }]
              : []
          }))
      )])
    const recyclers = [...last(this.states)?.recyclers ?? []]

    for (let pIdx = 0; pIdx < 2; pIdx++) {
      const army = units[pIdx]
      for (const unit of army) {
        const cell = cells[coordToIdx(unit.coord, this.globalData)]
        if (unit.strength > 1) {
          cell.history.push(`there were ${unit.strength} ${colors[pIdx]} units`)
        } else {
          cell.history.push(`there was 1 ${colors[pIdx]} unit`)
        }
      }
    }

    this.currentPartialData = { cells, units, recyclers }

    for (const event of dto.events) {
      const pStart = event.animData.start / frameInfo.frameDuration
      const pEnd = event.animData.end / frameInfo.frameDuration

      if (event.type === ev.BUILD) {
        this.handleEvent(event, {
          recycler: () => ({
            coord: event.coord,
            ownerIdx: event.playerIndex
          })
        },
        `a ${colors[event.playerIndex]} recycler is built`)
      } else if (event.type === ev.CELL_DAMAGE) {
        this.handleEvent(event, {
          cell: () => ({
            durability: event.amount
          })
        }, event.amount === 0 ? 'tile is no more' : null)
      } else if (event.type === ev.RECYCLER_FALL) {
        this.handleEvent(event, {
          recycler: () => null
        }, `a ${colors[event.playerIndex]} recycler gets disassembled`)
      } else if (event.type === ev.CELL_OWNER_SWAP) {
        this.handleEvent(event, {
          cell: () => ({
            ownerIdx: event.playerIndex
          })
        }, `tile becomes ${colors[event.playerIndex]}`)
      } else if (event.type === ev.UNIT_FALL) {
        this.handleEvent(event, {
          unit: () => ({
            strength: 0,
            p: pStart
          })
        }, `${event.amount} of ${colors[event.playerIndex]}'s units get disassembled`)
      } else if (event.type === ev.SPAWN) {
        this.handleEvent(event, {
          unit: (unit) => ({
            strength: (unit?.strength ?? 0) + event.amount,
            p: pEnd
          })
        }, event.amount > 1
          ? `${event.amount} ${colors[event.playerIndex]} units spawn here`
          : `1 ${colors[event.playerIndex]} unit spawns here`
        )
      } else if (event.type === ev.MOVE) {
        const angle = Math.atan2(event.target.y - event.coord.y, event.target.x - event.coord.x)

        // From
        this.handleEvent(event, {
          unit: (unit) => ({
            strength: unit.strength - event.amount,
            p: pStart
          })
        }, event.amount > 1
          ? `${event.amount} ${colors[event.playerIndex]} units MOVE out to (${event.target.x}, ${event.target.y})`
          : `1 ${colors[event.playerIndex]} unit MOVEs out to (${event.target.x}, ${event.target.y})`)

        // To
        this.handleEvent({
          ...event,
          coord: event.target
        }, {
          unit: (unit) => ({
            strength: (unit?.strength ?? 0) + event.amount,
            p: pEnd,
            angle
          }),
          cell: (cell) => ({
            lastAngle: { ...cell.lastAngle, [event.playerIndex]: angle }
          })
        }, event.amount > 1
          ? `${event.amount} ${colors[event.playerIndex]} units MOVE here from (${event.coord.x}, ${event.coord.y})`
          : `1 ${colors[event.playerIndex]} unit MOVEs here from (${event.coord.x}, ${event.coord.y})`)
      } else if (event.type === ev.FIGHT) {
        for (let pIdx = 0; pIdx < 2; pIdx++) {
          const isBattleWinner = event.amount > 0 && event.playerIndex === pIdx
          this.handleEvent({
            ...event,
            playerIndex: pIdx
          }, {
            unit: () => ({
              strength: isBattleWinner ? event.amount : 0,
              p: pStart
            }),
            cell: () => ({
              fight: true
            })
          }

          )
        }
      }

      event.animData.start /= frameInfo.frameDuration
      event.animData.end /= frameInfo.frameDuration
    }

    for (let pIdx = 0; pIdx < 2; ++pIdx) {
      const army = units[pIdx]
      for (const unit of army) {
        if (unit.foe == null) {
          const foeArmy = units[1 - pIdx]
          const foeUnit = foeArmy.find(foe => sameCoord(foe.coord, unit.coord))
          if (foeUnit != null) {
            unit.foe = foeUnit
            foeUnit.foe = unit
          }
        }
      }
    }

    for (let pIdx = 0; pIdx < 2; pIdx++) {
      const army = units[pIdx]
      for (const unit of army) {
        const cell = cells[coordToIdx(unit.coord, this.globalData)]

        if (cell.fight && unit.strength === 0 && (unit.foe?.strength ?? 0) === 0 && cell.durability > 0) {
          if (pIdx > 0) {
            cell.history.push('no units remain')
          }
        } else {
          if (cell.history.length === 1 && cell.history[0].startsWith('there')) {
            if (unit.strength === 1) {
              cell.history = [`1 ${colors[pIdx]} unit is here`]
            } else if (unit.strength > 0) {
              cell.history = [`${unit.strength} ${colors[pIdx]} units are here`]
            }
          } else {
            if (unit.strength === 1) {
              cell.history.push(`1 ${colors[pIdx]} unit remains`)
            } else if (unit.strength > 0) {
              cell.history.push(`${unit.strength} ${colors[pIdx]} units remain`)
            }
          }
        }
      }
    }

    for (const cell of cells) {
      cell.history = cell.history.map(v => ' • ' + v)
    }

    const recyclerTiles: RecyclerTile[] = []

    for (const recycler of recyclers) {
      recyclerTiles.push({
        ...recycler,
        events: []
      })
    }
    for (const e of dto.events) {
      if (e.type === ev.BUILD || e.type === ev.RECYCLER_FALL || e.type === ev.MATTER_COLLECT) {
        let t = recyclerTiles.find(r => sameCoord(r.coord, e.coord))
        if (t == null) {
          t = {
            coord: e.coord,
            events: [],
            ownerIdx: e.playerIndex
          }
          recyclerTiles.push(t)
        }
        t.events.push(e)
        if (e.type === ev.BUILD) {
          t.spawnAt = e.animData.start
        } else if (e.type === ev.RECYCLER_FALL) {
          t.unspawnAt = e.animData.start
        }
      }
    }

    const frameData: FrameData = {
      ...dto,
      cells,
      units,
      recyclers,
      recyclerTiles,
      previous: null
    }
    frameData.previous = last(this.states) ?? frameData

    this.states.push(frameData)
    return frameData
  }
}

const FONT_ANCHOR_OFFSET_Y = 0.24

function fitTextWithin (text: PIXI.BitmapText, RECT: { x: number, y: number, w: number, h: number }, place: (x: number) => number) {
  text.anchor = new PIXI.Point(0.5, 0.5 + FONT_ANCHOR_OFFSET_Y)
  const x = RECT.x + RECT.w / 2
  const y = RECT.y + RECT.h / 2
  text.position.set(place(x), y)
  if (text.width > RECT.w || text.height > RECT.h) {
    const coeff = fitAspectRatio(text.width, text.height, RECT.w, RECT.h)
    text.scale.set(coeff)
  }
}
