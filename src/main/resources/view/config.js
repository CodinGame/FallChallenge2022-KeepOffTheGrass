import { ViewModule, api } from './graphics/ViewModule.js'
import { EndScreenModule } from './endscreen-module/EndScreenModule.js';

// List of viewer modules that you want to use in your game
export const modules = [
  ViewModule,
  EndScreenModule
]

export const playerColors = [
  '#22a1e4', // curious blue
  '#ff1d5c' // radical red
]

export const options = [{
  title: 'CELL HISTORY',
  get: function () {
    return api.options.cellHistory
  },
  set: function (value) {
    api.options.cellHistory = value
    api.setCellHistory(value)
  },
  values: {
    'ON': true,
    'OFF': false
  }
}]


export const gameName = 'UTG2022'

export const stepByStepAnimateSpeed = 3
