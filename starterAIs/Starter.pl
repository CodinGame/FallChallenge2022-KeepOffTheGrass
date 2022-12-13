use strict;
use warnings;
use 5.32.1;
$|=1;

use constant ME => 1;
use constant OPP => 0;
use constant NONE => -1;

chomp(my $tokens=<>);
my ($width, $height) = split / /, $tokens;

my $turn = -1;
while (1) {
    $turn++;
    
    chomp($tokens=<>);
    my ($my_matter, $opp_matter) = split / /, $tokens;
    
    my (@tiles, @myTiles, @oppTiles, @neutralTiles, @myUnits, @oppUnits, @myRecyclers, @oppRecyclers);
    for my $j (0..$height-1) {
        for my $i (0..$width-1) {
            chomp($tokens=<>);
            my ($scrap, $owner, $units, $recycler, $can_build, $can_spawn, $in_range) = split / /, $tokens;
            my %tile = (
                x => $i,
                y => $j,
                scrap => $scrap,
                owner => $owner,
                units => $units,
                recycler => $recycler,
                canBuild => $can_build,
                canSpawn => $can_spawn,
                inRange => $in_range
            );
            
            push @tiles, \%tile;
            
            if ($tile{owner} == ME) {
                push @myTiles, \%tile;
                push @myUnits, \%tile if $tile{units} > 0;
                push @myRecyclers, \%tile if $tile{recycler};
            } elsif ($tile{owner} == OPP) {
                push @oppTiles, \%tile;
                push @oppUnits, \%tile if $tile{units} > 0;
                push @oppRecyclers, \%tile if $tile{recycler};
            } else {
                push @neutralTiles, \%tile;
            }
        }
    }
    
    my @actions;
    
    for my $tile (@myTiles) {
        if ($tile->{canSpawn}) {
            my $amount = 0;  #TODO: Pick amount of robots to spawn here.
            push @actions, "SPAWN $amount " . $tile->{x} . " " . $tile->{y} if $amount;
        }
        
        if ($tile->{canBuild}) {
            my $shouldBuild = 0;  #TODO: Pick whether to build recycler here.
            push @actions, "BUILD " . $tile->{x} . " " . $tile->{y} if $shouldBuild;
        }
    }
    
    for my $tile (@myUnits) {
        my $target = "";  #TODO: Pick a destination.
        if ($target) {
            my $amount = 0;  #TODO: Pick amount of units to move.
            push @actions, "MOVE $amount " . $tile->{x} . " " . $tile->{y} . " " . $target->{x} . " " . $target->{y};
        }
    }
    
   say @actions-0 == 0 ? "WAIT" : join ";", @actions;
}