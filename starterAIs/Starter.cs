using System;
using System.Linq;
using System.IO;
using System.Text;
using System.Collections;
using System.Collections.Generic;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Tile {
    public int x, y, scrapAmount, owner, units;
    public bool recycler, canBuild, canSpawn, inRangeOfRecycler;

    public Tile(int x, int y, int scrapAmount, int owner, int units, bool recycler, bool canBuild, bool canSpawn,
            bool inRangeOfRecycler) {
        this.x = x;
        this.y = y;
        this.scrapAmount = scrapAmount;
        this.owner = owner;
        this.units = units;
        this.recycler = recycler;
        this.canBuild = canBuild;
        this.canSpawn = canSpawn;
        this.inRangeOfRecycler = inRangeOfRecycler;
    }
}

class Player
{
    const int ME = 1;
    const int OPP = 0;
    const int NOONE = -1;

    static void Main(string[] args)
    {
        string[] inputs;
        inputs = Console.ReadLine().Split(' ');
        int width = int.Parse(inputs[0]);
        int height = int.Parse(inputs[1]);

        // game loop
        while (true)
        {
            List<Tile> tiles = new List<Tile>();
            List<Tile> myTiles = new List<Tile>();
            List<Tile> oppTiles = new List<Tile>();
            List<Tile> neutralTiles = new List<Tile>();
            List<Tile> myUnits = new List<Tile>();
            List<Tile> oppUnits = new List<Tile>();
            List<Tile> myRecyclers = new List<Tile>();
            List<Tile> oppRecyclers = new List<Tile>();

            inputs = Console.ReadLine().Split(' ');
            int myMatter = int.Parse(inputs[0]);
            int oppMatter = int.Parse(inputs[1]);
            for (int i = 0; i < height; i++)
            {
                for (int j = 0; j < width; j++)
                {
                    inputs = Console.ReadLine().Split(' ');
                    int scrapAmount = int.Parse(inputs[0]);
                    int owner = int.Parse(inputs[1]); // 1 = me, 0 = foe, -1 = neutral
                    int units = int.Parse(inputs[2]);
                    int recycler = int.Parse(inputs[3]);
                    int canBuild = int.Parse(inputs[4]);
                    int canSpawn = int.Parse(inputs[5]);
                    int inRangeOfRecycler = int.Parse(inputs[6]);

                    Tile tile = new Tile(
                            i,
                            j,
                            scrapAmount,
                            owner,
                            units,
                            recycler == 1,
                            canBuild == 1,
                            canSpawn == 1,
                            inRangeOfRecycler == 1);

                    tiles.Add(tile);

                    if (tile.owner == ME) 
                    {
                        myTiles.Add(tile);
                        if (tile.units > 0) 
                        {
                            myUnits.Add(tile);
                        } 
                        else if (tile.recycler) 
                        {
                            myRecyclers.Add(tile);
                        }
                    } 
                    else if (tile.owner == OPP) 
                    {
                        oppTiles.Add(tile);
                        if (tile.units > 0) 
                        {
                            oppUnits.Add(tile);
                        }
                        else if (tile.recycler) 
                        {
                            oppRecyclers.Add(tile);
                        }
                    } 
                    else 
                    {
                        neutralTiles.Add(tile);
                    }

                     
                }
            }

            List<String> actions = new List<String>();
            foreach (Tile tile in tiles) {
                if (tile.canSpawn) {
                    int amount = 1; // TODO: pick amount of robots to spawn here
                    if (amount > 0) {
                        actions.Add(String.Format("SPAWN {0} {1} {2}", amount, tile.x, tile.y));
                    }
                }
                if (tile.canBuild) {
                    bool shouldBuild = true; // TODO: pick whether to build recycler here
                    if (shouldBuild) {
                        actions.Add(String.Format("BUILD {0} {1}", tile.x, tile.y));
                    }
                }
            }

            foreach (Tile tile in myUnits) {
                Tile target = null; // TODO: pick a destination
                if (target != null) {
                    int amount = 0; // TODO: pick amount of units to move
                    actions.Add(String.Format("MOVE {0} {1} {2} {3} {4}", amount, tile.x, tile.y, target.x, target.y));
                }
            }

             if (actions.Count <= 0) {
                Console.WriteLine("WAIT");
            } else {
                Console.WriteLine(string.Join(";", actions.ToArray()));
                //actions.stream().collect(Collectors.joining(";"));
            }
        }
    }
}
