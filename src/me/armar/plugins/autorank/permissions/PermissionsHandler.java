package me.armar.plugins.autorank.permissions;

import org.bukkit.entity.Player;

/*
 * PermissionsHandler takes care of the communications with the permissions
 * plugin.
 * 
 */
public interface PermissionsHandler {

	/**
	 * Get all defined groups
	 * 
	 * @return an array of all groups defined in the config(s) of the permission
	 *         plugin.
	 */
	public String[] getGroups();

	public String[] getPlayerGroups(Player player);

	/**
	 * Gets the groups of the player in a world
	 * 
	 * @param player Player to get the groups from
	 * @param world World to get the world from
	 * @return an array containing all groups that the player is in.
	 */
	public String[] getWorldGroups(Player player, String world);

	public boolean replaceGroup(Player player, String world, String groupFrom, String groupTo);

	/**
	 * Sometimes replaceGroup does not work. You can then try to do it the
	 * reverse way, by demoting someone.
	 * 
	 * @param player Player to demote
	 * @param world On which world should we demote him? (null if every world)
	 * @param groupFrom What is the group he's currently in
	 * @param groupTo What is the group you want the player to demote to.
	 * @return true if properly demoted, false otherwise.
	 */
	public boolean demotePlayer(Player player, String world, String groupFrom, String groupTo);

	public String getName();

}
