use std::io;

macro_rules! parse_input {
    ($x:expr, $t:ident) => ($x.trim().parse::<$t>().unwrap())
}

const ME: i32 = 1;
const OPP: i32 = 0;
const NONE: i32 = -1;

#[derive(Clone, Debug, PartialEq)]
struct Tile {
    x: i32,
    y: i32,
    scrap_amount: i32,
    owner: i32,
    units: i32,
    recycler: bool,
    can_build: bool,
    can_spawn: bool,
    in_range_of_recycler: bool
}

fn main() {
    let mut input_line = String::new();
    io::stdin().read_line(&mut input_line).unwrap();
    let inputs = input_line.split(" ").collect::<Vec<_>>();
    let width = parse_input!(inputs[0], i32);
    let height = parse_input!(inputs[1], i32);

    // game loop
    loop {
        let mut tiles = Vec::new();
        let mut my_units = Vec::new();
        let mut opp_units = Vec::new();
        let mut my_recyclers = Vec::new();
        let mut opp_recyclers = Vec::new();
        let mut opp_tiles = Vec::new();
        let mut my_tiles = Vec::new();
        let mut neutral_tiles = Vec::new();


        let mut input_line = String::new();
        io::stdin().read_line(&mut input_line).unwrap();
        let inputs = input_line.split(" ").collect::<Vec<_>>();
        let _my_matter = parse_input!(inputs[0], i32);
        let _opp_matter = parse_input!(inputs[1], i32);
        for y in 0..height as i32 {
            for x in 0..width as i32 {
                let mut input_line = String::new();
                io::stdin().read_line(&mut input_line).unwrap();
                let inputs = input_line.split(" ").collect::<Vec<_>>();
                let scrap_amount = parse_input!(inputs[0], i32);
                let owner = parse_input!(inputs[1], i32); // 1 = me, 0 = foe, -1 = neutral
                let units = parse_input!(inputs[2], i32);
                let recycler = parse_input!(inputs[3], i32);
                let can_build = parse_input!(inputs[4], i32);
                let can_spawn = parse_input!(inputs[5], i32);
                let in_range_of_recycler = parse_input!(inputs[6], i32);

                let tile = Tile {x, y, scrap_amount, owner, units,
                     recycler: recycler == 1, can_build: can_build == 1, can_spawn: can_spawn == 1,
                     in_range_of_recycler: in_range_of_recycler == 1 };
                
                tiles.push(tile.clone());

                match tile.owner {
                    ME => {
                        my_tiles.push(tile.clone());
                        if tile.units > 0 { my_units.push(tile) } else if tile.recycler { my_recyclers.push(tile) }
                    }
                    OPP => {
                        opp_tiles.push(tile.clone());
                        if tile.units > 0 { opp_units.push(tile) } else if tile.recycler { opp_recyclers.push(tile) }

                    }
                    _ => neutral_tiles.push(tile)
                }
            }
        }

        let mut actions = Vec::new();
        let mut amount;
        let mut should_build;
        for tile in my_tiles {
            if tile.can_spawn {
                amount = 0;  // TODO: pick amount of robots to spawn here
                if amount > 0 {
                    actions.push(format!("SPAWN {} {} {}", amount, tile.x, tile.y));
                }
            }
            if tile.can_build {
                should_build = false; // TODO: pick whether to build recycler here
                if should_build {
                    actions.push(format!("BUILD {} {}", tile.x, tile.y));
                }
            }
        }
    
        for tile in my_units {
            let target = &tile; // TODO: pick a destination tile
            if target != &tile {
                amount = 0; // TODO: pick amount of units to move
                actions.push(format!("MOVE {} {} {} {} {}", amount, tile.x, tile.y, target.x, target.y));            
            }
        }

        if actions.len() > 0 {
            let res = actions.join(";");
            println!("{}", res);
        } else {
            println!("WAIT");
        }
    }
}
