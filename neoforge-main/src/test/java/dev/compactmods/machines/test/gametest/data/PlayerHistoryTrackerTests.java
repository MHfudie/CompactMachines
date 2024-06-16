package dev.compactmods.machines.test.gametest.data;

import dev.compactmods.machines.api.CompactMachines;
import dev.compactmods.machines.api.room.history.RoomEntryPoint;
import dev.compactmods.machines.player.PlayerEntryPointHistoryManager;
import dev.compactmods.machines.api.room.history.RoomEntryResult;
import dev.compactmods.machines.room.RoomCodeGenerator;
import dev.compactmods.machines.test.gametest.TestRoomApi;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

@PrefixGameTestTemplate(false)
@GameTestHolder(CompactMachines.MOD_ID)
public class PlayerHistoryTrackerTests {

    private static final String BATCH = "PLAYER_HISTORY_TRACKING";

    @GameTest(template = "empty_1x1", batch = BATCH)
    public static void failsPlayerGoingTooFar(final GameTestHelper test) {
        CompactMachines.Internal.ROOM_API = TestRoomApi.forTest();

        final var history = new PlayerEntryPointHistoryManager(1);

        final var player = test.makeMockPlayer(GameType.SURVIVAL);
        history.enterRoom(player, RoomCodeGenerator.generateRoomId(), RoomEntryPoint.nonexistent());

        final var tooFar = history.enterRoom(player, RoomCodeGenerator.generateRoomId(), RoomEntryPoint.nonexistent());

        test.assertTrue(tooFar == RoomEntryResult.FAILED_TOO_FAR_DOWN, "Room entry should have failed.");

        test.succeed();
    }

    @GameTest(template = "empty_1x1", batch = BATCH, timeoutTicks = 1400)
    public static void canGetPlayerHistory(final GameTestHelper test) throws InterruptedException {
        CompactMachines.Internal.ROOM_API = TestRoomApi.forTest();

        final var history = new PlayerEntryPointHistoryManager(5);

        final var player = test.makeMockPlayer(GameType.SURVIVAL);

        Deque<String> codes = new ArrayDeque<>(5);
        for (int i = 0; i < 5; i++) {
            var roomId = RoomCodeGenerator.generateRoomId();
            codes.push(roomId);
            history.enterRoom(player, roomId, RoomEntryPoint.nonexistent());

            Thread.sleep(250);
        }

        var hist = history.history(player)
                .limit(3)
                .collect(Collectors.toCollection(ArrayDeque::new));

        test.assertTrue(hist.size() == 3, "Expected 3 entries in history.");

        var mostRecent = hist.pop();
        test.assertTrue(mostRecent.roomCode().equals(codes.peek()), "Latest room code does not match.");

        test.succeed();
    }

    @GameTest(template = "empty_1x1", batch = BATCH, timeoutTicks = 1400)
    public static void canRemovePlayerHistory(final GameTestHelper test) throws InterruptedException {
        CompactMachines.Internal.ROOM_API = TestRoomApi.forTest();

        final var history = new PlayerEntryPointHistoryManager(5);
        final var player = test.makeMockPlayer(GameType.SURVIVAL);

        Deque<String> codes = new ArrayDeque<>(5);
        for (int i = 0; i < 5; i++) {
            var roomId = RoomCodeGenerator.generateRoomId();
            codes.push(roomId);
            history.enterRoom(player, roomId, RoomEntryPoint.nonexistent());

            Thread.sleep(250);
        }

        var oldHistoryAmount = history.history(player).count();

        history.popHistory(player, 1);

        var newHistoryAmount = history.history(player).count();

        test.assertTrue(oldHistoryAmount > newHistoryAmount, "History amount was not correct after pop.");

        test.succeed();
    }

    @GameTest(template = "empty_1x1", batch = BATCH, timeoutTicks = 1400)
    public static void testDataLogic(final GameTestHelper test) throws InterruptedException {
        CompactMachines.Internal.ROOM_API = TestRoomApi.forTest();

        final var history = new PlayerEntryPointHistoryManager(5);

        final var player = test.makeMockPlayer(GameType.SURVIVAL);

        Deque<String> codes = new ArrayDeque<>(5);
        for (int i = 0; i < 5; i++) {
            var roomId = RoomCodeGenerator.generateRoomId();
            codes.push(roomId);
            history.enterRoom(player, roomId, RoomEntryPoint.nonexistent());

            Thread.sleep(250);
        }

        long beforeSave = history.history(player.getUUID()).count();

        final var saved = history.codec()
            .encodeStart(NbtOps.INSTANCE, history)
            .getOrThrow();

        final var loaded = PlayerEntryPointHistoryManager.CODEC.parse(NbtOps.INSTANCE, saved).getOrThrow();

        long afterLoad = loaded.history(player.getUUID()).count();

        test.assertTrue(beforeSave == afterLoad, "History counts differ.");

        test.succeed();
    }
}
