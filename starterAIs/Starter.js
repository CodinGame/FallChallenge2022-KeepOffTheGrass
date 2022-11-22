const ME = 1
const OPP = 0
const NONE = -1

var inputs = readline().split(' ');
const width = parseInt(inputs[0]);
const height = parseInt(inputs[1]);

// game loop
while (true) {
    const tiles = []
    const myUnits = []
    const oppUnits = []
    const myRecyclers = []
    const oppRecyclers = []
    const oppTiles = []
    const myTiles = []
    const neutralTiles = []

    var inputs = readline().split(' ');
    const myMatter = parseInt(inputs[0]);
    const oppMatter = parseInt(inputs[1]);
    for (let y = 0; y < height; y++) {
        for (let x = 0; x < width; x++) {
            var inputs = readline().split(' ');
            const scrapAmount = parseInt(inputs[0]);
            const owner = parseInt(inputs[1]); // 1 = me, 0 = foe, -1 = neutral
            const units = parseInt(inputs[2]);
            const recycler = inputs[3] == '1';
            const canBuild = inputs[4] == '1';
            const canSpawn = inputs[5] == '1';
            const inRangeOfRecycler = inputs[6] == '1';

            const tile = {
                x,
                y,
                scrapAmount,
                owner,
                units,
                recycler,
                canBuild,
                canSpawn,
                inRangeOfRecycler
            }

            tiles.push(tile)

            if (tile.owner == ME) {
                myTiles.push(tile)
                if (tile.units > 0) {
                    myUnits.push(tile)
                } else if (tile.recycler) {
                    myRecyclers.push(tile)
                }
            } else if (tile.owner == OPP) {
                oppTiles.push(tile)
                if (tile.units > 0) {
                    oppUnits.push(tile)
                } else if (tile.recycler) {
                    oppRecyclers.push(tile)
                }
            } else {
                neutralTiles.push(tile)
            }
        }
    }


    const actions = []
    
    for (const tile of myTiles) {
        if (tile.canSpawn) {
            const amount = 0 //TODO: pick amount of robots to spawn here
            if (amount > 0) {
                actions.push(`SPAWN ${amount} ${tile.x} ${tile.y}`)
            }
        }
        if (tile.canBuild) {
            const shouldBuild = false //TODO: pick whether to build recycler here
            if (shouldBuild) {
                actions.push(`BUILD ${tile.x} ${tile.y}`)
            }
        }
    }
    

    for (const tile of myUnits) {
        const target = null //TODO: pick a destination
        if (target) {
            const amount = 0 //TODO: pick amount of units to move
            actions.push(`MOVE ${amount} ${tile.x} ${tile.y} ${target.x} ${target.y}`)
        }
    }

    console.log(actions.length > 0 ? actions.join(';') : 'WAIT');
}

