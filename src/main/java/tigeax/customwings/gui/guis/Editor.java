package tigeax.customwings.gui.guis;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tigeax.customwings.gui.CWGUIType;
import tigeax.customwings.CWPlayer;
import tigeax.customwings.CustomWings;
import tigeax.customwings.Settings;
import tigeax.customwings.wings.Wing;

public class Editor {

	Settings settings;

	public Editor() {
		settings = CustomWings.getSettings();
	}

	public void open(CWPlayer cwPlayer) {

		String guiName = settings.getEditorGUIName();
		int guiSize = settings.getMainGUISize();

		Inventory gui = Bukkit.createInventory(null, guiSize, guiName);

		for (Wing wing : CustomWings.getWings()) {

			ItemStack wingItem = wing.getGuiItem();
			int slot = wing.getGuiSlot();

			if (guiSize < slot) continue;

			gui.setItem(slot, wingItem);
		}

		ItemStack mainSettingsItem = settings.getEditorMainSettingsItem().clone();
		int mainSettingsSlot = settings.getEditorMainSettingsSlot();
		
		if (guiSize < mainSettingsSlot)
			gui.setItem(0, mainSettingsItem);
		else
			gui.setItem(mainSettingsSlot, mainSettingsItem);

		cwPlayer.getPlayer().openInventory(gui);
	}

	public void click(CWPlayer cwPlayer, String itemName, Integer clickedSlot) {

		// Open the Main Settings GUI
		if (itemName.equals("Main Settings")) {
			cwPlayer.openCWGUI(CWGUIType.EDITORMAINSETTINGS);
			return;
		}

		// Else open the Wing Settings GUI
		Wing wing = CustomWings.getWingByGUISlot(clickedSlot);
		cwPlayer.openCWGUI(CWGUIType.EDITORWINGSETTINGS, wing);
	}
}
