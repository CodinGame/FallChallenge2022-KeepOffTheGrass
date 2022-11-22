export type ContainerConsumer = (layer: PIXI.Container) => void

/**
 * Given by the SDK
 */
export interface FrameInfo {
  number: number
  frameDuration: number
  date: number
}
/**
 * Given by the SDK
 */
export interface CanvasInfo {
  width: number
  height: number
  oversampling: number
}
/**
 * Given by the SDK
 */
export interface PlayerInfo {
  name: string
  avatar: PIXI.Texture
  color: number
  index: number
  isMe: boolean
  number: number
  type?: string
}

export interface EventDto {
  type: number
  animData: AnimData

  coord?: CoordDto
  playerIndex?: number
  amount?: number
  target?: CoordDto
}

export interface PlayerDto {
  money: number
  cooldown: number
  message: string
}

export interface FrameDataDTO {
  players: PlayerDto[]
  events: EventDto[]
}

export interface FrameData extends FrameDataDTO {
  previous: FrameData
  units: UnitDto[][]
  cells: CellDto[]
  recyclers: RecyclerDto[]
  recyclerTiles: RecyclerTile[]
}

export interface RecyclerDto {
  coord: CoordDto
  ownerIdx: number
}

export interface CoordDto {
  x: number
  y: number
}

/* Deprecated */
export interface UnitHistory {
  n: number
  p: number
  angle?: number
}

export interface UnitDto {
  strength: number // Amount at end of turn
  coord: CoordDto

  /* Computed client side */
  stayed?: UnitHistory[]
  foe?: UnitDto
}

export interface CellDto {
  durability: number
  x: number
  y: number
  ownerIdx: number

  /* Computed client side */
  rand: number
  fight?: boolean
  history: string[]
  lastAngle?: Record<number, number>
}

export interface GlobalDataDTO {
  width: number
  height: number
  units: UnitDto[][]
  cells: CellDto[]
}
export interface GlobalData extends GlobalDataDTO {
  players: PlayerInfo[]
  playerCount: number
}

export interface AnimData {
  start: number
  end: number
}

export interface Effect {
  busy: boolean
  display: PIXI.DisplayObject
}

/* View entities */
export interface Tile {
  baseScale: number
  sprite: PIXI.Sprite
  overlay: PIXI.Sprite
  cracks: PIXI.Sprite[]
  border: PIXI.Sprite
  recycleFx: PIXI.AnimatedSprite
}

export interface Mutations {
  recycler?: () => RecyclerDto | null
  cell?: (cell: CellDto) => Partial<CellDto>
  unit?: (unit: UnitDto | null) => {strength: number, p: number, angle?: number}
}

export interface SpeechBubble {
  container: PIXI.Container
  speech: PIXI.Text
  show: boolean
}

export interface RecyclerTile extends RecyclerDto {
  spawnAt?: number
  unspawnAt?: number
  events: EventDto[]
}
