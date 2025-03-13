package com.holiday.minigolf;

import com.holiday.minigolf.listeners.*;
import com.raus.craftLib.CraftLib;
import com.holiday.minigolf.commands.ReloadCommand;
import com.holiday.minigolf.commands.ReloadCommandCompleter;
import com.holiday.minigolf.recipes.ItemRecipes;
import com.holiday.minigolf.task.GolfBallTask;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class Main extends JavaPlugin {

    public static Main instance;

    // Namespaced keys
    public NamespacedKey ballKey, putterKey, ironKey, driverKey, wedgeKey, whistleKey;
    public NamespacedKey parKey, xKey, yKey, zKey;

    //values
    public final double floorOffset = 0.05;

    CraftLib craftLib;

    private ReloadCommand reloadCommand;

    // Items
    public ItemStack golfBall, iron, driver, wedge, whistle, putter;

    public Map<Snowball, BukkitRunnable> ballRunnables = new HashMap<>();
    private Set<Material> slabMaterials = new HashSet<>();
    public List<Snowball> golfBalls = new LinkedList<>();
    public Map<UUID, Snowball> lastPlayerBall = new HashMap<>();

    @Override
    public void onEnable() {

        instance = this;

        // Load configuration
        saveDefaultConfig();
        reloadConfig();

        loadSlabMaterials();

        // Initialize keys
        initializeKeys();

        // Register commands
        reloadCommand = new ReloadCommand();
        Objects.requireNonNull(getCommand("minigolf")).setExecutor(reloadCommand);
        Objects.requireNonNull(this.getCommand("minigolf")).setTabCompleter(new ReloadCommandCompleter());

        // Register listeners
        registerListeners();

        // Initialize items
        initializeItems();

        // Register recipes
        ItemRecipes.registerRecipes();

        // Add keys to CraftLib if installed
        addKeysToCraftLib();

        // Schedule tasks
        new GolfBallTask().runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable() {
        for (BukkitRunnable runnable : ballRunnables.values()) {
            runnable.cancel();
        }
        for (Snowball snowballs : golfBalls) {
            snowballs.remove();
        }
    }

    private void initializeKeys() {
        ballKey = new NamespacedKey(this, "golf_ball");
        putterKey = new NamespacedKey(this, "putter");
        ironKey = new NamespacedKey(this, "iron");
        driverKey = new NamespacedKey(this, "driver");
        wedgeKey = new NamespacedKey(this, "wedge");
        whistleKey = new NamespacedKey(this, "return_whistle");

        parKey = new NamespacedKey(this, "par");
        xKey = new NamespacedKey(this, "x");
        yKey = new NamespacedKey(this, "y");
        zKey = new NamespacedKey(this, "z");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PuttListener(), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(), this);
        getServer().getPluginManager().registerEvents(new UnloadListener(), this);
        getServer().getPluginManager().registerEvents(new PowerMeter(this), this);
        getServer().getPluginManager().registerEvents(new PlaceListener(), this);
        // getServer().getPluginManager().registerEvents(new NoCollision(), this);
    }

    private void initializeItems() {
        iron = ItemRecipes.createIronItem();
        driver = ItemRecipes.createDriverItem();
        wedge = ItemRecipes.createWedgeItem();
        putter = ItemRecipes.createPutterItem();
        golfBall = ItemRecipes.createGolfBallItem();
        whistle = ItemRecipes.createWhistleItem();
    }

    private void addKeysToCraftLib() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CraftLib");
        if (plugin instanceof CraftLib) {
            // Si es una instancia de CraftLib, entonces puedes hacer el casteo
            craftLib = (CraftLib) plugin;
            // Ahora puedes acceder a los métodos de CraftLib según sea necesario
            craftLib.addKey(ironKey);
            craftLib.addKey(putterKey);
            craftLib.addKey(wedgeKey);
            craftLib.addKey(ballKey);
            craftLib.addKey(whistleKey);
        } else {
            getLogger().warning("CraftLib plugin not found!");
            // Manejo de error si no se encuentra el plugin CraftLib
        }
    }

    private void loadSlabMaterials() {
        FileConfiguration config = getConfig();
        ConfigurationSection slabSection = config.getConfigurationSection("blocks-slab");

        if (slabSection == null) {
            getLogger().warning("No 'blocks-slab' section found in config.");
            return;
        }

        for (String materialName : slabSection.getKeys(false)) {
            try {
                Material material = Material.matchMaterial(materialName);
                if (material == null) {
                    getLogger().warning("Material " + materialName + " in config is not valid.");
                    continue;
                }
                double impulse = slabSection.getDouble(materialName);
                slabMaterials.add(material);
            } catch (NumberFormatException e) {
                getLogger().warning("Invalid impulse value for material " + materialName + " in config.");
            }
        }
    }

    public boolean isSlab(Material blockType) {
        return slabMaterials.contains(blockType);
    }

    public ReloadCommand getReloadCommand() {
        return reloadCommand;
    }

    public static Main getInstance() {
        return instance;
    }

    public ItemStack getGolfBall() {
        return golfBall;
    }

    public Map<Snowball, BukkitRunnable> getBallRunnables() {
        return ballRunnables;
    }
}
