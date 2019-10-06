package com.c1games.terminal.starteralgo;

import com.c1games.terminal.algo.Config;
import com.c1games.terminal.algo.Coords;
import com.c1games.terminal.algo.GameIO;
import com.c1games.terminal.algo.io.GameLoop;
import com.c1games.terminal.algo.io.GameLoopDriver;
import com.c1games.terminal.algo.map.CannotRemoveException;
import com.c1games.terminal.algo.map.GameState;
import com.c1games.terminal.algo.map.MapBounds;
import com.c1games.terminal.algo.units.UnitType;

import java.util.*;

/**
 * Java implementation of the standard starter algo.
 */
public class StarterAlgo implements GameLoop {
    public static void main(String[] args) {
        new GameLoopDriver(new StarterAlgo()).run();
    }

    private static final Coords[] defensiveDestructorLocations = {
            new Coords(3, 13),
            new Coords(24, 13),
            new Coords(7, 10),
            new Coords(20, 10),
            new Coords(13, 10),
    };

    private static final Coords[] defensiveFilterLocations = {
            new Coords(2, 13),
            new Coords(4, 13),
            new Coords(23, 13),
            new Coords(25, 13),
            new Coords(6, 11),
            new Coords(8, 10),
            new Coords(21, 11),
            new Coords(19, 10),
            new Coords(12, 10),
            new Coords(14, 10),
    };

    private static final Coords[] laterFilters = {
            new Coords(5, 12),
            new Coords(22, 12),
            new Coords(1, 13),
            new Coords(26, 13),
            new Coords(15, 9),
            new Coords(15, 9),
            new Coords(18, 9),
            new Coords(19, 13),
            new Coords(9, 13),
    };

    private static final Coords[] laterDestructors = {
            new Coords(12, 9),
            new Coords(14, 9),
            new Coords(19, 9),
            new Coords(18, 8),
            new Coords(8, 9),
            new Coords(9, 8),
            new Coords(19, 12),
            new Coords(18, 12),
            new Coords(10, 12),
            new Coords(9, 12),
            new Coords(21, 10),
    };

    private static final Coords[] wallEncryptorLocations = {
            new Coords(24, 10),
            new Coords(22, 10),
            new Coords(21, 9),
            new Coords(20, 8),
            new Coords(19, 7),
            new Coords(18, 6),
            new Coords(17, 5),
            new Coords(16, 4),
            new Coords(12, 6),
            new Coords(11, 7),
            new Coords(10, 8),
            new Coords(9, 9),
            new Coords(23, 11),
    };

    @Override
    public void initialize(GameIO io, Config config) {
        io.debug().println("Configuring your custom java algo strategy...");
    }

    /**
     * Make a move in the game.
     */
    @Override
    public void onTurn(GameIO io, GameState move) {
        io.debug().println("Performing turn " + move.data.turnInfo.turnNumber + " of your custom algo strategy");
        buildDefenses(move);
        if (move.data.turnInfo.turnNumber >= 4 && move.data.turnInfo.turnNumber % 4 == 0) {
            deployAttackers(move);
        }
    }

    /**
     * Once the C1 logo is made, attempt to build some defenses.
     */
    private void buildDefenses(GameState move) {
        /*
        First lets protect ourselves a little with destructors.
         */
        move.attemptSpawnMultiple(Arrays.asList(defensiveDestructorLocations), UnitType.Destructor);

        /*
        Then lets boost our offense by building some encryptors to shield
        our information units. Lets put them near the front because the
        shields decay over time, so shields closer to the action
        are more effective.
         */
        move.attemptSpawnMultiple(Arrays.asList(defensiveFilterLocations),UnitType.Filter);

        if (move.data.turnInfo.turnNumber >= 4 && move.data.turnInfo.turnNumber % 4 == 0) {
            move.attemptSpawnMultiple(Arrays.asList(wallEncryptorLocations), UnitType.Encryptor);
        }
        try {
            for (Coords c : (wallEncryptorLocations)) {
                move.removeFirewall(c);
            }
        } catch (CannotRemoveException c) {

        }

        if (move.data.turnInfo.turnNumber >= 5 && move.data.turnInfo.turnNumber % 5 == 0) {
            for (Coords c : laterFilters) {
                if ((move.data.p1Stats.cores-3) >= 13) {
                    move.attemptSpawn(c, UnitType.Filter);
                }
            }
            for (Coords c : laterDestructors) {
                if ((move.data.p1Stats.cores-2) >= 13) {
                    move.attemptSpawn(c, UnitType.Destructor);
                }
            }
        }

        /*
        Lastly lets build encryptors in random locations. Normally building
        randomly is a bad idea but we'll leave it to you to figure out better
        strategies.

        First we get all locations on the bottom half of the map
        that are in the arena bounds.
         */
        // List<Coords> possibleSpawnPoints = new ArrayList<>();
        // for (int x = 0; x < MapBounds.BOARD_SIZE; x++) {
        //     for (int y = 0; y < MapBounds.BOARD_SIZE; y++) {
        //         Coords c = new Coords(x, y);
        //         if (move.canSpawn(c, UnitType.Encryptor, 1).affirmative())
        //             possibleSpawnPoints.add(c);
        //     }
        // }
        // Collections.shuffle(possibleSpawnPoints);
        // while (move.numberAffordable(UnitType.Encryptor) > 0) {
        //     Coords c = possibleSpawnPoints.remove(possibleSpawnPoints.size() - 1);
        //     move.spawn(c, UnitType.Encryptor);
        // }
    }

    /**
     * Deploy offensive units.
     */
    private void deployAttackers(GameState move) {
        if (move.data.turnInfo.turnNumber > 20) {
            while (move.data.p1Stats.bits > 0) {
                move.attemptSpawn(new Coords(23, 9), UnitType.Ping);
            }
        } else {
            for (int i = 0; i < (move.data.p1Stats.bits); i++) {
                move.attemptSpawn(new Coords(22, 8), UnitType.Ping);
            }
        }


        // /*
        // First lets check if we have 10 bits, if we don't we lets wait for
        // a turn where we do.
        //  */
        // if (move.data.p1Stats.bits < 10)
        //     return;
        //
        // /*
        // Then lets deploy an EMP long range unit to destroy firewalls for us.
        //  */
        // move.attemptSpawn(new Coords(3, 10), UnitType.EMP);
        //
        // /*
        // Now lets send out 3 Pings to hopefully score, we can spawn multiple
        // information units in the same location.
        // */
        // for (int i = 0; i < 3; i++) {
        //     move.attemptSpawn(new Coords(14, 0), UnitType.Ping);
        // }
        //
        // /*
        // NOTE: the locations we used above to spawn information units may become
        // blocked by our own firewalls. We'll leave it to you to fix that issue
        // yourselves.
        //
        // Lastly lets send out Scramblers to help destroy enemy information units.
        // A complex algo would predict where the enemy is going to send units and
        // develop its strategy around that. But this algo is simple so lets just
        // send out scramblers in random locations and hope for the best.
        //
        // Firstly information units can only deploy on our edges. So lets get a
        // list of those locations.
        //  */
        // List<Coords> friendlyEdges = new ArrayList<>();
        // friendlyEdges.addAll(Arrays.asList(MapBounds.EDGE_LISTS[MapBounds.EDGE_BOTTOM_LEFT]));
        // friendlyEdges.addAll(Arrays.asList(MapBounds.EDGE_LISTS[MapBounds.EDGE_BOTTOM_RIGHT]));
        //
        // /*
        // While we have remaining bits to spend lets send out scramblers randomly.
        // */
        // while (move.numberAffordable(UnitType.Scrambler) > 1) {
        //     Coords c = friendlyEdges.get(rand.nextInt(friendlyEdges.size()));
        //     move.attemptSpawn(c, UnitType.Scrambler);
        //
        //     /*
        //     We don't have to remove the location since multiple information
        //     units can occupy the same space.
        //      */
        // }
    }

}
