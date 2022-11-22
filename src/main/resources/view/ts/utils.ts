import * as utils from '../core/utils.js'

export function setAnimationProgress (fx: PIXI.AnimatedSprite, progress: number): void {
  let idx = Math.floor(progress * fx.totalFrames)
  idx = Math.min(fx.totalFrames - 1, idx)
  fx.gotoAndStop(idx)
}

export function fit (entity, maxWidth, maxHeight) {
  entity.scale.set(utils.fitAspectRatio(entity.texture.width, entity.texture.height, maxWidth, maxHeight))
}

export interface Point {
  x: number
  y: number
}

export function setSize (sprite: PIXI.Sprite | PIXI.Container, size: number) {
  sprite.width = size
  sprite.height = size
}

export function bounce (t: number): number {
  return 1 + (Math.sin(t * 10) * 0.5 * Math.cos(t * 3.14 / 2)) * (1 - t) * (1 - t)
}

export function generateText (text, color, size) {
  const drawnText = new PIXI.Text(text, {
    fontSize: Math.round(size) + 'px',
    // fontFamily: 'Arial',
    fontWeight: 'bold',
    fill: color,
    lineHeight: Math.round(size)
  })
  drawnText.anchor.x = 0.5
  drawnText.anchor.y = 0.5
  return drawnText
}

export function last<T> (arr: T[]): T {
  return arr[arr.length - 1]
}

export function key (x: number, y: number): string {
  return `${x},${y}`
}
export function angleDiff (a, b) {
  return Math.abs(utils.lerpAngle(a, b, 0) - utils.lerpAngle(a, b, 1))
}

export function randomChoice (rand: number, coeffs: number[]): number {
  const total = coeffs.reduce((a, b) => a + b, 0)
  const b = 1 / total
  const weights = coeffs.map(v => v * b)
  let cur = 0
  for (let i = 0; i < weights.length; ++i) {
    cur += weights[i]
    if (cur >= rand) {
      return i
    }
  }
  return 0
}
