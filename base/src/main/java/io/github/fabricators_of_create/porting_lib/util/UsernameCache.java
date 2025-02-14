package io.github.fabricators_of_create.porting_lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Caches player's last known usernames
 * <p>
 * Modders should use {@link #getLastKnownUsername(UUID)} to determine a players
 * last known username.<br>
 * For convenience, {@link #getMap()} is provided to get an immutable copy of
 * the caches underlying map.
 */
public final class UsernameCache {

	private static Map<UUID, String> map = new HashMap<>();

	private static final Path saveFile = FabricLoader.getInstance().getGameDir().resolve("usernamecache.json");
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static final Logger LOGGER = LogManager.getLogger(UsernameCache.class);
	private static final Marker USRCACHE = MarkerManager.getMarker("USERNAMECACHE");

	private UsernameCache() {}

	/**
	 * Set a player's current usernamee
	 *
	 * @param uuid
	 *            the player's {@link java.util.UUID UUID}
	 * @param username
	 *            the player's username
	 */
	public static void setUsername(UUID uuid, String username) {
		Objects.requireNonNull(uuid);
		Objects.requireNonNull(username);

		if (username.equals(map.get(uuid))) return;

		map.put(uuid, username);
	}

	/**
	 * Remove a player's username from the cache
	 *
	 * @param uuid
	 *            the player's {@link java.util.UUID UUID}
	 * @return if the cache contained the user
	 */
	protected static boolean removeUsername(UUID uuid) {
		Objects.requireNonNull(uuid);

		if (map.remove(uuid) != null) {
			return true;
		}

		return false;
	}

	/**
	 * Get the player's last known username
	 * <p>
	 * <b>May be <code>null</code></b>
	 *
	 * @param uuid
	 *            the player's {@link java.util.UUID UUID}
	 * @return the player's last known username, or <code>null</code> if the
	 *         cache doesn't have a record of the last username
	 */
	@Nullable
	public static String getLastKnownUsername(UUID uuid) {
		Objects.requireNonNull(uuid);
		return map.get(uuid);
	}

	/**
	 * Check if the cache contains the given player's username
	 *
	 * @param uuid
	 *            the player's {@link java.util.UUID UUID}
	 * @return if the cache contains a username for the given player
	 */
	public static boolean containsUUID(UUID uuid) {
		Objects.requireNonNull(uuid);
		return map.containsKey(uuid);
	}

	/**
	 * Get an immutable copy of the cache's underlying map
	 *
	 * @return the map
	 */
	public static Map<UUID, String> getMap() {
		return ImmutableMap.copyOf(map);
	}

	public static void init() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> load());
	    ServerLifecycleEvents.SERVER_STOPPED.register(server -> save());
	}

	private static void save() {
		try {
			Files.writeString(saveFile, gson.toJson(map));
		} catch (IOException e) {
			LOGGER.error(USRCACHE, "Could not save username cache!", e);
		}
	}

	/**
	 * Load the cache from file
	 */
	public static void load() {
		if (!Files.exists(saveFile)) return;

		try (final BufferedReader reader = Files.newBufferedReader(saveFile, Charsets.UTF_8)) {
			Type type = new TypeToken<Map<UUID, String>>(){}.getType();
			map = gson.fromJson(reader, type);
		} catch (JsonSyntaxException | IOException e) {
			LOGGER.error(USRCACHE,"Could not parse username cache file as valid json, deleting file {}", saveFile, e);
			try {
				Files.delete(saveFile);
			}
			catch (IOException e1)
			{
				LOGGER.error(USRCACHE,"Could not delete file {}", saveFile.toString());
			}
		} finally {
			// Can sometimes occur when the json file is malformed
			if (map == null) {
				map = new HashMap<>();
			}
		}
	}
}
