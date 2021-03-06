package me.armar.plugins.autorank.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.rankbuilder.ChangeGroup;
import me.armar.plugins.autorank.rankbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * <b>Autorank's API class:</b>
 * <p>
 * You, as a developer, can you use this class to get data from players or data
 * about groups. The API is never finished and if you want to see something
 * added, tell us!
 * <p>
 * 
 * @author Staartvin
 * 
 */
public class API {

	private final Autorank plugin;

	public API(final Autorank instance) {
		plugin = instance;
	}

	/**
	 * Get the addon manager of Autorank.
	 * <p>
	 * This class stores information about the loaded addons
	 * 
	 * @return {@link me.armar.plugins.autorank.addons.AddOnManager} class
	 */
	public AddOnManager getAddonManager() {
		return plugin.getAddonManager();
	}

	/**
	 * Gets all {@linkplain RequirementsHolder}s for a player at the exact
	 * moment.
	 * This does not consider already finished requirement but just mirrors the
	 * config file.
	 * 
	 * @param player Player to get the requirements from.
	 * @return a list of {@linkplain RequirementsHolder}s; An empty list when
	 *         none are found.
	 */
	public List<RequirementsHolder> getAllRequirements(final Player player) {
		return plugin.getPlayerChecker().getAllRequirementsHolders(player);
	}

	/**
	 * Gets all {@linkplain RequirementsHolder}s that are not yet completed.
	 * 
	 * @param player Player to get the failed requirements for.
	 * @return list of {@linkplain RequirementsHolder}s that still have to be
	 *         completed.
	 */
	public List<RequirementsHolder> getFailedRequirementsHolders(final Player player) {
		return plugin.getPlayerChecker().getFailedRequirementsHolders(player);
	}

	/**
	 * Gets the global play time (playtime across all servers with the same
	 * MySQL database linked) of a player.
	 * <p>
	 * 
	 * @deprecated use getGlobalPlayTime(UUID uuid) instead.
	 * @param player Player to check for.
	 * @return play time of a player. -1 if no entry was found.
	 */
	@Deprecated
	public int getGlobalPlayTime(final Player player) {
		final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());
		//UUIDManager.getUUIDFromPlayer(player.getName());

		return getGlobalPlayTime(uuid);
	}

	public int getGlobalPlayTime(final UUID uuid) {
		return plugin.getPlaytimes().getGlobalTime(uuid);
	}

	/**
	 * Gets the local play time of this player on this server according to
	 * Autorank. (in minutes)<br>
	 * This method will grab the time from the data.yml used by Autorank and
	 * <br>
	 * this is not dependend on other plugins.
	 * 
	 * @deprecated Use getLocalPlayTime(UUID uuid) instead.
	 * @param player Player to get the time for.
	 * @return play time of this player or 0 if not found.
	 */
	@Deprecated
	public int getLocalTime(final Player player) {
		final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());
		//UUIDManager.getUUIDFromPlayer(player.getName());

		return plugin.getPlaytimes().getLocalTime(uuid);
	}

	/**
	 * @see #getLocalTime(Player)
	 **/
	public int getLocalPlayTime(final UUID uuid) {
		return plugin.getPlaytimes().getLocalTime(uuid);
	}

	/**
	 * Gets the database name Autorank stores its global times in.
	 * 
	 * @return name of database
	 */
	public String getMySQLDatabase() {
		return plugin.getMySQLWrapper().getDatabaseName();
	}

	/**
	 * Gets the permission group that the player will be ranked up to after
	 * he completes all requirements.
	 * <p>
	 * <b>NOTE:</b> This does not mean the player will always be ranked up to
	 * this group. If a requirement has its own <i>'rank change'</i> result, the
	 * player will be ranked up to that group and not the 'global results'
	 * group.
	 * 
	 * @param player Player to get the next rank up for.
	 * @return The name of the group the player will be ranked to; null when no
	 *         rank up.
	 */
	@Deprecated
	public String getNextRankupGroup(final Player player) {
		// Do not rank a player when he is excluded
		if (AutorankTools.isExcluded(player))
			return null;

		// only first group - will cause problems
		final String groupName = plugin.getPermPlugHandler().getPrimaryGroup(player);

		final List<ChangeGroup> changes = plugin.getPlayerChecker().getChangeGroupManager().getChangeGroups(groupName);

		if (changes == null || changes.size() == 0) {
			return null;
		}

		String chosenPath = plugin.getPlayerDataHandler().getChosenPath(player.getUniqueId());

		if (!plugin.getPlayerDataHandler().checkValidChosenPath(player)) {
			chosenPath = "unknown";
		}

		final ChangeGroup changeGroup = plugin.getPlayerChecker().getChangeGroupManager().matchChangeGroup(groupName,
				chosenPath);

		if (changeGroup == null)
			return null;

		return changeGroup.getNextGroup();
	}

	/**
	 * Gets the permission groups a player is part of.
	 * 
	 * @param player Player to get the groups of
	 * @return A list of permission groups
	 */
	public List<String> getPermissionGroups(final Player player) {
		final String[] groups = plugin.getPermPlugHandler().getPermissionPlugin().getPlayerGroups(player);

		final List<String> permGroups = new ArrayList<String>();

		// Convert array into list
		for (final String group : groups) {
			permGroups.add(group);
		}

		return permGroups;
	}

	/**
	 * Gets the primary permissions group of a player.
	 * 
	 * @param player Player to get the primary group of
	 * @return Name of the group that seems primary for Autorank.
	 */
	public String getPrimaryGroup(final Player player) {
		return plugin.getPermPlugHandler().getPrimaryGroup(player);
	}

	/**
	 * Gets the local play time (playtime on this server) of a player. <br>
	 * The time given depends on what plugin is used for keeping track of time.
	 * <br>
	 * The time is always given in seconds.
	 * <p>
	 * 
	 * @param player Player to get the time for
	 * @return play time of a player. 0 when has never played before.
	 */
	public int getTimeOfPlayer(final Player player) {
		return plugin.getPlaytimes().getTimeOfPlayer(player.getName(), true);
	}

	/**
	 * Register a requirement that can be used in the advanced config.
	 * The name should be unique as that is the way Autorank will identify the
	 * requirement.
	 * <p>
	 * The name will be the name that is used in the config.
	 * 
	 * @param uniqueName Unique name identifier for the requirement
	 * @param clazz Requirement class that does all the logic
	 */
	public void registerRequirement(final String uniqueName, final Class<? extends Requirement> clazz) {
		plugin.getLogger().info("Loaded custom requirement: " + uniqueName);

		plugin.registerRequirement(uniqueName, clazz);
	}

	/**
	 * Register a result that can be used in the advanced config.
	 * The name should be unique as that is the way Autorank will identify the
	 * result.
	 * <p>
	 * The name will be the name that is used in the config.
	 * 
	 * @param uniqueName Unique name identifier for the result
	 * @param clazz Result class that does all the logic
	 */
	public void registerResult(final String uniqueName, final Class<? extends Result> clazz) {
		plugin.getLogger().info("Loaded custom result: " + uniqueName);

		plugin.registerResult(uniqueName, clazz);
	}
}
