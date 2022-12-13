STDOUT.sync = true # DO NOT REMOVE

ME = 1
OPP = 0
NONE = -1

width, height = gets.split.map &:to_i

# game loop
loop {
  tiles = []
  my_units = []
  opp_units = []
  my_recyclers = []
  opp_recyclers = []
  opp_tiles = []
  my_tiles = []
  neutral_tiles = []

  my_matter, opp_matter = gets.split.map &:to_i
  height.times { |y|
    width.times { |x|
     # owner: 1 = me, 0 = foe, -1 = neutral
     scrap_amount, owner, units, recycler, can_build, can_spawn, in_range_of_recycler = gets.split.map &:to_i

     tile = {
       scrap_amount: scrap_amount,
       owner: owner,
       units: units,
       recycler: recycler,
       can_build: can_build==1,
       can_spawn: can_spawn==1,
       in_range_of_recycler: in_range_of_recycler==1
     }

     tiles.append(tile)

     if tile.owner == ME
         my_tiles.append(tile)
         if tile.units > 0
             my_units.append(tile)
         elsif tile.recycler
             my_recyclers.append(tile)
         end
     elsif tile.owner == OPP
         opp_tiles.append(tile)
         if tile.units > 0
             opp_units.append(tile)
         elsif tile.recycler
             opp_recyclers.append(tile)
         end
     else
         neutral_tiles.append(tile)
     end
    }
  }

  actions = []
  my_tiles.each { |tile|
    if tile.can_spawn
      amount = 0 # TODO: pick amount of robots to spawn here
      if amount > 0
          actions<<"SPAWN #{amount} #{tile.x} #{tile.y}"
      end
    end
    if tile.can_build
        should_build = false # TODO: pick whether to build recycler here
        if should_build
            actions<<"BUILD #{tile.x} #{tile.y}"
        end
    end
  }

  my_units.each { |tile|
    target = nil # TODO: pick a destination tile
    if taget
      amount = 0 # TODO: pick amount of units to move
      actions<<"MOVE #{amount} #{tile.x} #{tile.y} #{target.x} #{target.y}"
    end
  }

  # To debug: STDERR.puts "Debug messages..."
  puts actions.size > 0 ? actions*";" : "WAIT"
}