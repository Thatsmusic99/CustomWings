package tigeax.customwings.command;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import tigeax.customwings.gui.CWGUIType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tigeax.customwings.CWPlayer;
import tigeax.customwings.CustomWings;
import tigeax.customwings.wings.Wing;

import java.util.HashMap;

/*
 * Main command for CustomWings
 */

public class Wings implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

		// Open the wing gui if no arguments are given
		if (args.length == 0) {
			openMainGUI(sender);
			return true;
		}

		// If arguments are given switch between possible commands

		switch (args[0]) {

			case "setwing":
				setWing(sender, args);
				return true;

			case "preview":
			case "p":
				preview(sender);
				return true;

			case "edit":
			case "e":
				openEditorGUI(sender);
				return true;

			case "reload":
			case "r":
				reload(sender);
				return true;

			case "takeaway":
				takeAwayWings(sender, args);
				return true;

			default:
				openMainGUI(sender);
				return true;
		}
	}

	// Open the wings GUI
	public void openMainGUI(CommandSender sender) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(CustomWings.getMessages().getNotAPlayerError());
			return;
		}

		if (!sender.hasPermission("customwings.command")) {
			sender.sendMessage(CustomWings.getMessages().getNoPermissionForCommand());
			return;
		}

		Player player = (Player) sender;
		CustomWings.getCWPlayer(player).openCWGUI(CWGUIType.WINGSELECT, 0);
	}

	// Set a wing for another player
	public void setWing(CommandSender sender, String[] args) {

		if (!sender.hasPermission("customwings.setwing")) {
			sender.sendMessage(CustomWings.getMessages().getNoPermissionForCommand());
			return;
		}

		// If no player is specified send an error
		if (args.length == 1) {
			sender.sendMessage(CustomWings.getMessages().getMissingArgumentsSetWing());
			return;
		}

		Player wingSetPlayer = Bukkit.getPlayer(args[1]);

		// Send an error if the supplied playername doesn't exist
		if (wingSetPlayer == null) {
			sender.sendMessage(CustomWings.getMessages().getInvalidPlayerError(args[1]));
			return;
		}

		CWPlayer cwPlayer = CustomWings.getCWPlayer(wingSetPlayer);

		// If no wing was specified set the wing to null
		if (args.length == 2) {
			cwPlayer.setEquippedWing(null);
			sender.sendMessage(CustomWings.getMessages().getSetWingCommandWingSet(wingSetPlayer.getName(), "none"));
			return;
		}

		// If an invalid wing was specified set the wing to null
		Wing wing = CustomWings.getWingByID(args[2]);

		if (wing == null) {
			cwPlayer.setEquippedWing(null);
			sender.sendMessage(CustomWings.getMessages().getSetWingCommandWingSet(wingSetPlayer.getName(), "none"));
			return;
		}

		// If the wing was valid let the player equip the wing
		cwPlayer.setEquippedWing(wing);
		sender.sendMessage(CustomWings.getMessages().getSetWingCommandWingSet(wingSetPlayer.getName(), wing.getID()));
	}

	// Preview a wing
	public void preview(CommandSender sender) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(CustomWings.getMessages().getNotAPlayerError());
			return;
		}

		if (!sender.hasPermission("customwings.preview")) {
			sender.sendMessage(CustomWings.getMessages().getNoPermissionForCommand());
			return;
		}

		Player player = (Player) sender;
		CWPlayer cwPlayer = CustomWings.getCWPlayer(player);

		Wing wing = cwPlayer.getEquippedWing();

		// Send an error if the player does not have a wing equipped
		if (wing == null) {
			player.sendMessage(CustomWings.getMessages().getNoWingToPreview());
			return;
		}
		
		// If the player is already previewing a wing stop previewing
		if (cwPlayer.getEquippedWing().isPreviewing(player)) {
			wing.removeFromPreview(player);
			return;
		}

		wing.addToPreview(player);
	}

	// Open the editor GUI
	public void openEditorGUI(CommandSender sender) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(CustomWings.getMessages().getNotAPlayerError());
			return;
		}

		if (!sender.hasPermission("customwings.editor")) {
			sender.sendMessage(CustomWings.getMessages().getNoPermissionForCommand());
			return;
		}

		// Open the editor GUI
		CWPlayer cwPlayer = CustomWings.getCWPlayer((Player) sender);
		cwPlayer.openCWGUI(CWGUIType.LASTEDITORGUI);
	}

	// Reload CustomWings
	public void reload(CommandSender sender) {
		
		if (!sender.hasPermission("customwings.reload")) {
			sender.sendMessage(CustomWings.getMessages().getNoPermissionForCommand());
			return;
		}

		CustomWings.reload();
		sender.sendMessage(CustomWings.getMessages().getReloadSuccess());
	}

	//Take wings away from player
	public void takeAwayWings(CommandSender sender, String[] args) {
		if (sender.hasPermission("customwings.takeawaywings")) {

			// If no player or wing ID is specified send an error
			if (args.length == 1 || args.length == 2) {
				sender.sendMessage(CustomWings.getMessages().getMissingArgumentsTakeAwayWing());

			} else if (args.length == 3) {

				if (CustomWings.isVaultEnabled()) {

					try {
						OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
						Wing wing = CustomWings.getWingByID(args[2]);
						if (wing != null) {
							CustomWings.getPermissions().playerRemove(null, player.getPlayer(), "customwings.wing."+wing.getID().toLowerCase());
							sender.sendMessage(CustomWings.getMessages().getWingsRemoved(args[1], wing));
						} else {
							sender.sendMessage(CustomWings.getMessages().getInvalidWingsError(args[2]));
						}
					} catch (NullPointerException e) {
						sender.sendMessage(CustomWings.getMessages().getInvalidPlayerError(args[1]));
					}
				} else {
					sender.sendMessage("Vault is needed for that action");
				}

			} else {
				sender.sendMessage(CustomWings.getMessages().getMissingArgumentsTakeAwayWing());
			}

		} else {
			sender.sendMessage(CustomWings.getMessages().getNoPermissionForCommand());
		}
	}

}
