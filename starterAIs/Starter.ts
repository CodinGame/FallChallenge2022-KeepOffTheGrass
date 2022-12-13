//Types
interface Tile {
  x: number;
  y: number;
  scrapAmount: number;
  owner: number;
  units: number;
  recycler: number;
  canBuild: boolean;
  canSpawn: boolean;
  inRangeOfRecycler: boolean;
}
type TileList = Tile[];

//Consts
const ME = 1,
  OPP = 0,
  NONE = -1;

const [width, height] = readline().split(" ").map(Number);

// game loop
while (true) {
  const tiles: TileList = [];
  const myUnits: TileList = [];
  const oppUnits: TileList = [];
  const myRecyclers: TileList = [];
  const oppRecyclers: TileList = [];
  const oppTiles: TileList = [];
  const myTiles: TileList = [];
  const neutralTiles: TileList = [];

  const [myMatter, oppMatter] = readline().split(" ").map(Number);
  for (let y = 0; y < height; y++) {
    for (let x = 0; x < width; x++) {
      const [
        scrapAmount,
        owner,
        units,
        recycler,
        canBuild,
        canSpawn,
        inRangeOfRecycler,
      ] = readline().split(" ").map(Number);

      const tile: Tile = {
        x,
        y,
        scrapAmount,
        owner,
        units,
        recycler,
        canBuild: canBuild === 1,
        canSpawn: canSpawn === 1,
        inRangeOfRecycler: inRangeOfRecycler === 1,
      };

      tiles.push(tile);

      if (tile.owner === ME) {
        myTiles.push(tile);
        if (tile.units > 0) {
          myUnits.push(tile);
        } else if (tile.recycler) {
          myRecyclers.push(tile);
        }
      } else if (tile.owner === OPP) {
        oppTiles.push(tile);
        if (tile.units > 0) {
          oppUnits.push(tile);
        } else if (tile.recycler) {
          oppRecyclers.push(tile);
        }
      } else {
        neutralTiles.push(tile);
      }
    }
  }

  const actions: string[] = [];

  for (const tile of myTiles) {
    if (tile.canSpawn) {
      const amount = 0; //TODO: pick amount of robots to spawn here
      if (amount > 0) {
        actions.push(`SPAWN ${amount} ${tile.x} ${tile.y}`);
      }
    }
    if (tile.canBuild) {
      const shouldBuild = false; //TODO: pick whether to build recycler here
      if (shouldBuild) {
        actions.push(`BUILD ${tile.x} ${tile.y}`);
      }
    }
  }

  for (const tile of myUnits) {
    const target: Tile | null = null; //TODO: pick a destination
    if (target) {
      const amount = 0; //TODO: pick amount of units to move
      actions.push(
        `MOVE ${amount} ${tile.x} ${tile.y} ${target.x} ${target.y}`
      );
    }
  }

  console.log(actions.length > 0 ? actions.join(";") : "WAIT");
}
