package main

import (
	"fmt"
	"strings"
)

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
type Tile struct {
	x                 int
	y                 int
	scrapAmount       int
	owner             int
	units             int
	recycler          bool
	canBuild          bool
	canSpawn          bool
	inRangeOfRecycler bool
}

const me = 1
const foe = 0
const neutral = -1

func main() {
	var width, height int
	fmt.Scan(&width, &height)

	// game loop
	for {
		var tiles []Tile
		var myTiles []Tile
		var oppTiles []Tile
		var neutralTiles []Tile
		var myUnits []Tile
		var oppUnits []Tile
		var myRecyclers []Tile
		var oppRecyclers []Tile

		var myMatter, oppMatter int
		fmt.Scan(&myMatter, &oppMatter)

		for i := 0; i < height; i++ {
			for j := 0; j < width; j++ {
				// owner: 1 = me, 0 = foe, -1 = neutral
				var scrapAmount, owner, units, recycler, canBuild, canSpawn, inRangeOfRecycler int
				fmt.Scan(&scrapAmount, &owner, &units, &recycler, &canBuild, &canSpawn, &inRangeOfRecycler)

				tile := Tile{
					i,
					j,
					scrapAmount,
					owner,
					units,
					recycler == 1,
					canBuild == 1,
					canSpawn == 1,
					inRangeOfRecycler == 1,
				}

				tiles = append(tiles, tile)

				if tile.owner == me {
					myTiles = append(myTiles, tile)

					if tile.units > 0 {
						myUnits = append(myUnits, tile)
					} else if tile.recycler {
						myRecyclers = append(myRecyclers, tile)
					}
				} else if tile.owner == foe {
					oppTiles = append(oppTiles, tile)

					if tile.units > 0 {
						oppUnits = append(oppUnits, tile)
					} else if tile.recycler {
						oppRecyclers = append(oppRecyclers, tile)
					} else {
						neutralTiles = append(neutralTiles, tile)
					}
				}
			}
		}

		// calculate actions
		var actions []string

		for _, tile := range tiles {

			if tile.canSpawn {
				amount := 1 // TODO: pick amount of robots to spawn here
				if amount > 0 {
					actions = append(actions, fmt.Sprintf("SPAWN %d %d %d", amount, tile.x, tile.y))
				}
			}

			if tile.canBuild {
				shouldBuild := true // TODO: pick whether to build recycler here
				if shouldBuild {
					actions = append(actions, fmt.Sprintf("BUILD %d %d", tile.x, tile.y))
				}
			}
		}

		for _, tile := range myUnits {
			// TODO: pick a destination
			targetX := -1
			targetY := -1
			if targetX != -1 && targetY != -1 {
				amount := 0 // TODO: pick amount of units to move
				actions = append(actions, fmt.Sprintf("MOVE %d %d %d %d %d", amount, tile.x, tile.y, targetX, targetY))
			}
		}

		// fmt.Fprintln(os.Stderr, "Debug messages...")
		if len(actions) <= 0 {
			fmt.Println("WAIT")
		} else {
			fmt.Println(strings.Join(actions, ";"))
		}
	}
}
