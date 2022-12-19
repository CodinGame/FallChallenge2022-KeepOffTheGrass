<?php
define("ME", 1);
define("OPP", 0);
define("NONE", -1);

fscanf(STDIN, "%d %d", $width, $height);

// game loop
while (TRUE)
{
    $tiles = array();
    $myTiles = array();
    $oppTiles = array();
    $neutralTiles = array();
    $myUnits = array();
    $oppUnits = array();
    $myRecyclers = array();
    $oppRecyclers = array();
    
    fscanf(STDIN, "%d %d", $myMatter, $oppMatter);
    for ($i = 0; $i < $height; $i++)
    {
        for ($j = 0; $j < $width; $j++)
        {
            // $owner: 1 = me, 0 = foe, -1 = neutral
            fscanf(STDIN, "%d %d %d %d %d %d %d", $scrapAmount, $owner, $units, $recycler, $canBuild, $canSpawn, $inRangeOfRecycler);

            $tile = new Tile(
                $j,
                $i,
                $scrapAmount,
                $owner,
                $units,
                $recycler == 1,
                $canBuild == 1,
                $canSpawn == 1,
                $inRangeOfRecycler == 1);
            $tiles[] = $tile;

            if ($tile->owner == ME) {
                $myTiles[] = $tile;
                if ($tile->units > 0) {
                    $myUnits[] = $tile;
                } else if ($tile->recycler) {
                    $myRecyclers[] = $tile;
                }
            } else if ($tile->owner == OPP) {
                $oppTiles[] = $tile;
                if ($tile->units > 0) {
                    $oppUnits[] = $tile;
                } else if ($tile->recycler) {
                    $oppRecyclers[] = $tile;
                }
            } else {
                $neutralTiles[] = $tile;
            }
        }
    }

    $actions = array();
    foreach($myTiles as $tile) {
        if ($tile->canSpawn) {
            $amount = 0; // TODO: pick amount of robots to spawn here
            if ($amount > 0) {
                $action = "SPAWN " . $amount . " " . $tile->x . " " . $tile->y;
                $actions[] = $action;
            }
        }
        if ($tile->canBuild) {
            $shouldBuild = false; // TODO: pick whether to build recycler here
            if ($shouldBuild) {
                $action = "BUILD " . $tile->x . " " . $tile->y;
                $actions[] = $action;             
            }
        }
    }

    foreach($myUnits as $tile) {
        $target = null; // TODO: pick a destination
        if ($target != null) {
            $amount = 0; // TODO: pick amount of units to move
            $action = "MOVE " . $amount . " " . $tile->x . " " . $tile->y . " " . $target->x . " " . $target->y;
            $actions[] = $action;
        }
    }

        // Write an action using echo(). DON'T FORGET THE TRAILING \n
        // To debug: error_log(var_export($var, true)); (equivalent to var_dump)
        if (empty($actions)) {
            echo("WAIT\n");
        } else {
            $action_str = implode(";", $actions);
            echo($action_str."\n");
        }
}

class Tile {
    public $x, $y, $scrapAmount, $owner, $units;
    public $recycler, $canBuild, $canSpawn, $inRangeOfRecycler;

    public function __construct(int $x, int $y, int $scrapAmount, int $owner, int $units, bool $recycler, bool $canBuild, bool $canSpawn, bool $inRangeOfRecycler) {
        $this->x = $x;
        $this->y = $y;
        $this->scrapAmount = $scrapAmount;
        $this->owner = $owner;
        $this->units = $units;
        $this->recycler = $recycler;
        $this->canBuild = $canBuild;
        $this->canSpawn = $canSpawn;
        $this->inRangeOfRecycler = $inRangeOfRecycler;
    }
}
?>
