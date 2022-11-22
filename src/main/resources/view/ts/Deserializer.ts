import { CellDto, RecyclerDto, FrameDataDTO, GlobalDataDTO, PlayerDto, UnitDto, EventDto } from './types.js'

const MAIN_SEPARATOR = ';'

function splitLine (str) {
  return str.length === 0 ? [] : str.split(' ')
}

export function parseData (unsplit: string, globalData: GlobalDataDTO): FrameDataDTO {
  const raw = unsplit.split(MAIN_SEPARATOR)
  let idx = 0

  const players: PlayerDto[] = []
  for (let i = 0; i < 2; ++i) {
    const rawPlayer = splitLine(raw[idx++])
    players.push({
      money: +rawPlayer[0],
      cooldown: +rawPlayer[1],
      message: rawPlayer.slice(2).join(' ')
    })
  }

  const events: EventDto[] = []
  const eventCount = +raw[idx++]
  for (let i = 0; i < eventCount; ++i) {
    const playerIndex = +raw[idx++]
    const amount = +raw[idx++]
    const rawCoord = splitLine(raw[idx++])
    const coord = {
      x: +rawCoord[0],
      y: +rawCoord[1]
    }
    const rawTarget = splitLine(raw[idx++])
    const target = {
      x: +rawTarget[0],
      y: +rawTarget[1]
    }
    const type = +raw[idx++]
    const start = +raw[idx++]
    const end = +raw[idx++]
    const animData = { start, end }

    events.push({
      playerIndex,
      amount,
      coord,
      target,
      type,
      animData
    })
  }

  const parsed = {
    players,
    events
  }

  return parsed
}

export function parseGlobalData (unsplit: string): GlobalDataDTO {
  const raw = unsplit.split(MAIN_SEPARATOR)
  let idx = 0
  const width = +raw[idx++]
  const height = +raw[idx++]

  const cells: CellDto[] = []
  for (let x = 0; x < width; ++x) {
    for (let y = 0; y < height; ++y) {
      const rawCell = splitLine(raw[idx++])
      const cell: CellDto = {
        x,
        y,
        durability: +rawCell[0],
        ownerIdx: +rawCell[1],
        history: [],
        rand: Math.random()
      }
      cells.push(cell)
    }
  }

  const units: UnitDto[][] = []
  for (let pIdx = 0; pIdx < 2; ++pIdx) {
    const unitCount = +raw[idx++]
    const playerUnits: UnitDto[] = []
    for (let i = 0; i < unitCount; ++i) {
      const rawUnit = splitLine(raw[idx++])
      playerUnits.push({
        coord: {
          x: +rawUnit[0],
          y: +rawUnit[1]
        },
        strength: +rawUnit[2]
      })
    }
    units.push(playerUnits)
  }
  const parsed = {
    cells,
    units,
    width,
    height
  }
  return parsed
}
