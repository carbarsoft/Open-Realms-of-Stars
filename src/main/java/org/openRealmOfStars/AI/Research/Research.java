package org.openRealmOfStars.AI.Research;

import org.openRealmOfStars.player.PlayerInfo;
import org.openRealmOfStars.player.SpaceRace.SpaceRace;
import org.openRealmOfStars.player.diplomacy.Attitude;
import org.openRealmOfStars.player.ship.ShipComponentType;
import org.openRealmOfStars.player.ship.ShipHullType;
import org.openRealmOfStars.player.ship.ShipSize;
import org.openRealmOfStars.player.ship.ShipStat;
import org.openRealmOfStars.player.ship.generator.ShipGenerator;
import org.openRealmOfStars.player.ship.shipdesign.ShipDesign;
import org.openRealmOfStars.player.tech.Tech;
import org.openRealmOfStars.player.tech.TechType;
import org.openRealmOfStars.starMap.StarMap;
import org.openRealmOfStars.starMap.planet.BuildingFactory;
import org.openRealmOfStars.starMap.planet.construction.Building;
import org.openRealmOfStars.utilities.DiceGenerator;

/**
 *
 * Open Realm of Stars game project
 * Copyright (C) 2016  Tuomo Untinen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/
 *
 *
 * AI handling for research
 *
 */

public final class Research {

  /**
   * Hiding the default constructor
   */
  private Research() {
    // Nothing to do
  }

  /**
   * Default focus level when dividing focus evenly. Well one tech
   * level gets then 20 but all others 16.
   */
  public static final int DEFAULT_FOCUS_LEVEL = 16;

  /**
   * Default focus level when dividing focus evenly. This is the
   * higher default one.
   */
  public static final int HIGH_FOCUS_LEVEL = 20;

  /**
   * Default focus level when dividing focus evenly. This is the
   * highest default one.
   */
  public static final int VERY_HIGH_FOCUS_LEVEL = 40;

  /**
   * Default focus level when some races have more than one high focuses.
   */
  public static final int LOW_FOCUS_LEVEL = 12;

  /**
   * Handle new ship designs for AI
   * @param info PlayerInfo
   */
  public static void handleShipDesigns(final PlayerInfo info) {
    handleBattleShipDesign(info, ShipSize.SMALL, false);
    handleBattleShipDesign(info, ShipSize.MEDIUM, false);
    handleBattleShipDesign(info, ShipSize.LARGE, false);
    handleBattleShipDesign(info, ShipSize.HUGE, false);
    handleBattleShipDesign(info, ShipSize.MEDIUM, true);
    handleBattleShipDesign(info, ShipSize.LARGE, true);
    handleBattleShipDesign(info, ShipSize.HUGE, true);
    handleStarbaseDesign(info, ShipSize.SMALL);
    handleStarbaseDesign(info, ShipSize.MEDIUM);
    handleStarbaseDesign(info, ShipSize.LARGE);
    handleStarbaseDesign(info, ShipSize.HUGE);
    handleTrooperShipDesign(info);
    handleColonyShipDesign(info);
    handleFreighterShipDesign(info);
    handlePrivateerShipDesign(info);
    handleSpyShipDesign(info);
  }

  /**
   * Remove Unused and obsolete ship design. Ship design must be one
   * which is being obsoleted and hasn't been built single time.
   * This delete is lazy. It only deletes one design at time, but it
   * should be enough since not every turn there is a new design done.
   * @param info Player information
   * @param map Starmap for checking that ship design isn't being
   *            built anywhere.
   */
  public static void removeUnusedAndObsoleteDesigns(final PlayerInfo info,
      final StarMap map) {
    for (ShipStat stat : info.getShipStatList()) {
      if (stat.isObsolete() && stat.getNumberOfBuilt() == 0
          && !map.isShipStatBeingBuilt(stat, info)) {
        info.removeShipStat(stat);
        break;
      }
    }
  }

  /**
   * Handle Battle ship design for AI for certain size
   * @param info Player
   * @param size ShipSize to handle
   * @param bomber Force bomber ship design
   */
  private static void handleBattleShipDesign(final PlayerInfo info,
      final ShipSize size, final boolean bomber) {
    ShipDesign design = ShipGenerator.createBattleShip(info, size, bomber);
    if (design != null) {
      ShipStat[] stats = info.getShipStatList();
      boolean notFound = true;
      for (ShipStat stat : stats) {
        if (stat.getDesign().getHull().getSize() == size && stat.getDesign()
            .getHull().getHullType() == ShipHullType.NORMAL
            && !stat.isObsolete()) {
          notFound = false;
          if (design.getTotalMilitaryPower() > stat.getDesign()
              .getTotalMilitaryPower() && !stat.isObsolete()) {
            stat.setObsolete(true);
            ShipStat ship = new ShipStat(design);
            info.addShipStat(ship);
            break;
          }
        }
      }
      if (notFound) {
        ShipStat ship = new ShipStat(design);
        info.addShipStat(ship);
      }
    }

  }

  /**
   * Handle Privateer ship design for AI
   * @param info Player
   */
  private static void handlePrivateerShipDesign(final PlayerInfo info) {
    ShipDesign designMedium = ShipGenerator.createPrivateerShip(info,
        ShipSize.MEDIUM);
    ShipDesign designLarge = ShipGenerator.createPrivateerShip(info,
        ShipSize.LARGE);
    ShipDesign design = null;
    if (designLarge != null) {
      design = designLarge;
    } else {
      design = designMedium;
    }
    if (design != null) {
      ShipStat[] stats = info.getShipStatList();
      boolean notFound = true;
      for (ShipStat stat : stats) {
        if (stat.getDesign().getHull().getHullType() == ShipHullType.PRIVATEER
            && !stat.isObsolete()) {
          notFound = false;
          if (design.getTotalMilitaryPower() > stat.getDesign()
              .getTotalMilitaryPower() && !stat.isObsolete()) {
            stat.setObsolete(true);
            ShipStat ship = new ShipStat(design);
            info.addShipStat(ship);
            break;
          }
        }
      }
      if (notFound) {
        ShipStat ship = new ShipStat(design);
        info.addShipStat(ship);
      }
    }

  }

  /**
   * Handle Trooper ship design for AI
   * @param info Player
   */
  private static void handleTrooperShipDesign(final PlayerInfo info) {
    ShipDesign design = ShipGenerator.createColony(info, true);
    if (design != null) {
      ShipStat[] stats = info.getShipStatList();
      boolean notFound = true;
      for (ShipStat stat : stats) {
        if (stat.getDesign().getHull().getHullType() == ShipHullType.FREIGHTER
            && stat.getDesign()
                .gotCertainType(ShipComponentType.PLANETARY_INVASION_MODULE)
            && !stat.isObsolete()) {
          notFound = false;
          if (design.getTotalTrooperPower() > stat.getDesign()
              .getTotalTrooperPower()) {
            stat.setObsolete(true);
            ShipStat ship = new ShipStat(design);
            info.addShipStat(ship);
            break;
          }
        }
      }
      if (notFound) {
        ShipStat ship = new ShipStat(design);
        info.addShipStat(ship);
      }
    }

  }

  /**
   * Handle Spy ship design for AI
   * @param info Player
   */
  private static void handleSpyShipDesign(final PlayerInfo info) {
    ShipDesign design = ShipGenerator.createSpy(info);
    if (design != null && info.researchSpyShips()) {
      ShipStat[] stats = info.getShipStatList();
      boolean notFound = true;
      for (ShipStat stat : stats) {
        if (stat.getDesign().getName().startsWith("Spy Mk")
            && stat.getDesign()
                .gotCertainType(ShipComponentType.ESPIONAGE_MODULE)
            && !stat.isObsolete()) {
          notFound = false;
          if (design.getEspionagePower() > stat.getDesign()
              .getEspionagePower()) {
            stat.setObsolete(true);
            ShipStat ship = new ShipStat(design);
            info.addShipStat(ship);
            break;
          }
        }
      }
      if (notFound) {
        ShipStat ship = new ShipStat(design);
        info.addShipStat(ship);
      }
    }
  }

  /**
   * Handle Trooper ship design for AI
   * @param info Player
   */
  private static void handleColonyShipDesign(final PlayerInfo info) {
    ShipDesign design = ShipGenerator.createColony(info, false);
    if (design != null) {
      ShipStat[] stats = info.getShipStatList();
      boolean notFound = true;
      for (ShipStat stat : stats) {
        if (stat.getDesign().getHull().getHullType() == ShipHullType.FREIGHTER
            && stat.getDesign()
                .gotCertainType(ShipComponentType.COLONY_MODULE)
            && !stat.isObsolete()) {
          notFound = false;
          if (design.getTotalColonyPower() > stat.getDesign()
              .getTotalColonyPower()) {
            stat.setObsolete(true);
            ShipStat ship = new ShipStat(design);
            info.addShipStat(ship);
            break;
          }
        }
      }
      if (notFound) {
        ShipStat ship = new ShipStat(design);
        info.addShipStat(ship);
      }
    }

  }

  /**
   * Handle Freighter ship design for AI
   * @param info Player
   */
  private static void handleFreighterShipDesign(final PlayerInfo info) {
    ShipDesign design = ShipGenerator.createFreighter(info);
    if (design != null) {
      ShipStat[] stats = info.getShipStatList();
      boolean notFound = true;
      for (ShipStat stat : stats) {
        if (stat.getDesign().getHull().getHullType() == ShipHullType.FREIGHTER
            && !stat.getDesign()
                .gotCertainType(ShipComponentType.COLONY_MODULE)
            && !stat.getDesign()
               .gotCertainType(ShipComponentType.PLANETARY_INVASION_MODULE)
            && !stat.isObsolete()) {
          notFound = false;
          if (design.getFreeSlots() > stat.getDesign().getFreeSlots()) {
            stat.setObsolete(true);
            ShipStat ship = new ShipStat(design);
            info.addShipStat(ship);
            break;
          }
        }
      }
      if (notFound) {
        ShipStat ship = new ShipStat(design);
        info.addShipStat(ship);
      }
    }

  }

  /**
   * Handle Starbase design for AI for certain size
   * @param info Player
   * @param size ShipSize to handle
   */
  private static void handleStarbaseDesign(final PlayerInfo info,
      final ShipSize size) {
    if (size == ShipSize.SMALL && info.isBiggerStarbases()) {
      ShipStat[] stats = info.getShipStatList();
      for (ShipStat stat : stats) {
        if (stat.getDesign().getHull().getSize() == ShipSize.SMALL
            && stat.getDesign().getHull().getHullType()
            == ShipHullType.STARBASE && !stat.isObsolete()) {
          // Once bigger starbases are available, small once are obsolete
          stat.setObsolete(true);
        }
      }
      return;
    }
    ShipDesign design = ShipGenerator.createStarbase(info, size);
    if (design != null) {
      ShipStat[] stats = info.getShipStatList();
      boolean notFound = true;
      for (ShipStat stat : stats) {
        if (stat.getDesign().getHull().getSize() == size && stat.getDesign()
            .getHull().getHullType() == ShipHullType.STARBASE
            && !stat.isObsolete()) {
          notFound = false;
          if (design.getTotalMilitaryPower() > stat.getDesign()
              .getTotalMilitaryPower() && design.getStarbaseValue() == stat
              .getDesign().getStarbaseValue()) {
            stat.setObsolete(true);
            ShipStat ship = new ShipStat(design);
            info.addShipStat(ship);
            break;
          }
          if (design.getTotalMilitaryPower() == stat.getDesign()
              .getTotalMilitaryPower() && design.getStarbaseValue() > stat
              .getDesign().getStarbaseValue()) {
            stat.setObsolete(true);
            ShipStat ship = new ShipStat(design);
            info.addShipStat(ship);
            break;
          }
        }
      }
      if (notFound) {
        ShipStat ship = new ShipStat(design);
        info.addShipStat(ship);
      }
    }

  }

  /**
   * Check Update combat tech
   * @param info PlayerInfo
   * @param attitude Player's attitude
   */
  protected static void checkUpdateCombat(final PlayerInfo info,
      final Attitude attitude) {
    int level = info.getTechList().getTechLevel(TechType.Combat);
    Tech[] missingTechs = info.getTechList().getListMissingTech(TechType.Combat,
        level);
    boolean mustHaveTech = false;
    boolean missileTech = false;
    for (Tech missingTech : missingTechs) {
      if (missingTech.getName().contains("module")
          || missingTech.getName().contains("Orbital")) {
        mustHaveTech = true;
      }
      if (missingTech.getName().contains("missile")) {
        missileTech = true;
      }
    }
    if (!mustHaveTech) {
      if (attitude == Attitude.AGGRESSIVE) {
        if (missingTechs.length == 1) {
          level = level + 1;
          info.getTechList().setTechLevel(TechType.Combat, level);
        }
      } else if (attitude == Attitude.MILITARISTIC) {
        if (!missileTech) {
          level = level + 1;
          info.getTechList().setTechLevel(TechType.Combat, level);
        }
      } else if (attitude == Attitude.SCIENTIFIC) {
        if (DiceGenerator.getRandom(99) < 20) {
          level = level + 1;
          info.getTechList().setTechLevel(TechType.Combat, level);
        }
      } else if (DiceGenerator.getRandom(99) < 30) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Combat, level);
      }
    }
  }
  /**
   * Check Update defense tech
   * @param info PlayerInfo
   * @param attitude Player's attitude
   */
  protected static void checkUpdateDefense(final PlayerInfo info,
      final Attitude attitude) {
    int level = info.getTechList().getTechLevel(TechType.Defense);
    Tech[] missingTechs = info.getTechList().getListMissingTech(
        TechType.Defense, level);
    boolean turretTech = false;
    boolean generatorTech = false;
    boolean armorTech = false;
    for (Tech missingTech : missingTechs) {
      if (missingTech.getName().contains("turret")) {
        turretTech = true;
      }
      if (missingTech.getName().contains("generator")) {
        generatorTech = true;
      }
      if (missingTech.getName().contains("Armor")) {
        armorTech = true;
      }
    }
    if (attitude == Attitude.AGGRESSIVE) {
      if (missingTechs.length == 1) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Defense, level);
      }
    } else if (attitude == Attitude.MILITARISTIC) {
      if (!generatorTech && !armorTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Defense, level);
      }
    } else if (attitude == Attitude.PEACEFUL) {
      if (!turretTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Defense, level);
      }
    } else if (attitude == Attitude.SCIENTIFIC) {
      if (DiceGenerator.getRandom(99) < 20) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Defense, level);
      }
    } else if (DiceGenerator.getRandom(99) < 30) {
      level = level + 1;
      info.getTechList().setTechLevel(TechType.Defense, level);
    }
  }
  /**
   * Check Update hull tech
   * @param info PlayerInfo
   * @param attitude Player's attitude
   */
  protected static void checkUpdateHull(final PlayerInfo info,
      final Attitude attitude) {
    int level = info.getTechList().getTechLevel(TechType.Hulls);
    Tech[] missingTechs = info.getTechList().getListMissingTech(
        TechType.Hulls, level);
    boolean freighterTech = false;
    boolean starbaseTech = false;
    boolean privateerTech = false;
    boolean probeTech = false;
    boolean battleTech = false;
    for (Tech missingTech : missingTechs) {
      if (missingTech.getName().contains("freighter")) {
        freighterTech = true;
      }
      if (missingTech.getName().contains("starbase")) {
        starbaseTech = true;
      }
      if (missingTech.getName().contains("Privateer")) {
        privateerTech = true;
      }
      if (missingTech.getName().contains("Probe")) {
        probeTech = true;
      }
      if (missingTech.getName().contains("Battle")) {
        battleTech = true;
      }
    }
    if (attitude == Attitude.AGGRESSIVE) {
      if (!freighterTech && level > 1) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Hulls, level);
      }
    } else if (attitude == Attitude.MILITARISTIC) {
      if (!battleTech && !freighterTech && level > 1) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Hulls, level);
      }
    } else if (attitude == Attitude.MERCHANTICAL) {
      if (!freighterTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Hulls, level);
      }
    } else if (attitude == Attitude.EXPANSIONIST) {
      if (!probeTech && !freighterTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Hulls, level);
      }
    } else if (attitude == Attitude.BACKSTABBING) {
      if (!privateerTech && level > 1) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Hulls, level);
      }
    } else if (attitude == Attitude.DIPLOMATIC) {
      if (!starbaseTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Hulls, level);
      }
    } else if (attitude == Attitude.SCIENTIFIC) {
      if (DiceGenerator.getRandom(99) < 20) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Hulls, level);
      }
    } else if (DiceGenerator.getRandom(99) < 30) {
      level = level + 1;
      info.getTechList().setTechLevel(TechType.Hulls, level);
    }
  }
  /**
   * Check Update planetary improvemnt tech
   * @param info PlayerInfo
   * @param attitude Player's attitude
   */
  protected static void checkUpdateImprovement(final PlayerInfo info,
      final Attitude attitude) {
    int level = info.getTechList().getTechLevel(TechType.Improvements);
    Tech[] missingTechs = info.getTechList().getListMissingTech(
        TechType.Improvements, level);
    boolean labTech = false;
    boolean farmTech = false;
    boolean mineTech = false;
    boolean cultTech = false;
    boolean credTech = false;
    boolean prodTech = false;
    boolean starbaseTech = false;
    for (Tech missingTech : missingTechs) {
      Building build = BuildingFactory.createByName(
          missingTech.getImprovement());
      if (build != null) {
        if (build.getFarmBonus() > 0) {
          farmTech = true;
          if (info.getRace() == SpaceRace.MECHIONS) {
            // Mechions do not care about farm tech
            farmTech = false;
          }
        }
        if (build.getReseBonus() > 0) {
          labTech = true;
        }
        if (build.getCultBonus() > 0) {
          cultTech = true;
        }
        if (build.getCredBonus() > 0) {
          credTech = true;
        }
        if (build.getMineBonus() > 0) {
          mineTech = true;
        }
        if (build.getFactBonus() > 0) {
          prodTech = true;
        }
      }
      if (missingTech.getName().contains("Starbase")) {
        starbaseTech = true;
      }
      if (missingTech.getName().contains("Basic lab")) {
        // No need to update if basic lab is missing
        return;
      }
    }
    if (attitude == Attitude.MILITARISTIC) {
      if (!prodTech && !mineTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Improvements, level);
      }
    } else if (attitude == Attitude.DIPLOMATIC) {
      if (!cultTech && !credTech && !starbaseTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Improvements, level);
      }
    } else if (attitude == Attitude.PEACEFUL) {
      if (!cultTech && !farmTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Improvements, level);
      }
    } else if (attitude == Attitude.EXPANSIONIST) {
      if (!prodTech && !farmTech && !mineTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Improvements, level);
      }
    } else if (attitude == Attitude.MERCHANTICAL) {
      if (!credTech && !labTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Improvements, level);
      }
    } else if (attitude == Attitude.SCIENTIFIC) {
      if (!labTech && !starbaseTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Improvements, level);
      }
    } else if (DiceGenerator.getRandom(99) < 30) {
      level = level + 1;
      info.getTechList().setTechLevel(TechType.Improvements, level);
    }
  }
  /**
   * Check Update propulsion tech
   * @param info PlayerInfo
   * @param attitude Player's attitude
   */
  protected static void checkUpdatePropulsion(final PlayerInfo info,
      final Attitude attitude) {
    int level = info.getTechList().getTechLevel(TechType.Propulsion);
    Tech[] missingTechs = info.getTechList().getListMissingTech(
        TechType.Propulsion, level);
    boolean nuclearTech = false;
    boolean warpTech = false;
    boolean hyperTech = false;
    boolean impulseTech = false;
    boolean fighterBayTech = false;
    for (Tech missingTech : missingTechs) {
      if (missingTech.getName().contains("Nuclear")) {
        nuclearTech = true;
      }
      if (missingTech.getName().contains("Warp")) {
        warpTech = true;
      }
      if (missingTech.getName().contains("Hyper")) {
        hyperTech = true;
      }
      if (missingTech.getName().contains("Impulse")) {
        impulseTech = true;
      }
      if (missingTech.getName().contains("Fighter bay")) {
        fighterBayTech = true;
      }
    }
    if (attitude == Attitude.AGGRESSIVE) {
      if (missingTechs.length == 1  && !fighterBayTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Propulsion, level);
      }
    } else if (attitude == Attitude.MILITARISTIC) {
      if (!nuclearTech && !fighterBayTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Propulsion, level);
      }
    } else if (attitude == Attitude.MERCHANTICAL) {
      if (!warpTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Propulsion, level);
      }
    } else if (attitude == Attitude.LOGICAL) {
      if (!hyperTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Propulsion, level);
      }
    } else if (attitude == Attitude.EXPANSIONIST) {
      if (!warpTech && !impulseTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Propulsion, level);
      }
    } else if (attitude == Attitude.SCIENTIFIC) {
      if (DiceGenerator.getRandom(99) < 20) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Propulsion, level);
      }
    } else if (DiceGenerator.getRandom(99) < 30) {
      level = level + 1;
      info.getTechList().setTechLevel(TechType.Propulsion, level);
    }
  }
  /**
   * Check Update electronics tech
   * @param info PlayerInfo
   * @param attitude Player's attitude
   */
  protected static void checkUpdateElectronics(final PlayerInfo info,
      final Attitude attitude) {
    int level = info.getTechList().getTechLevel(TechType.Electrics);
    Tech[] missingTechs = info.getTechList().getListMissingTech(
        TechType.Electrics, level);
    boolean cloakTech = false;
    boolean scannerTech = false;
    boolean longScannerTech = false;
    boolean planetaryScannerTech = false;
    boolean targetingComputerTech = false;
    boolean jammerTech = false;
    for (Tech missingTech : missingTechs) {
      if (missingTech.getName().contains("Cloaking")) {
        cloakTech = true;
      }
      if (missingTech.getName().contains("Scanner")) {
        scannerTech = true;
      }
      if (missingTech.getName().contains("LR scanner")) {
        longScannerTech = true;
      }
      if (missingTech.getName().contains("Planetary scanner")) {
        planetaryScannerTech = true;
      }
      if (missingTech.getName().contains("Targeting computer")) {
        targetingComputerTech = true;
      }
      if (missingTech.getName().contains("Jammer")) {
        jammerTech = true;
      }
    }
    if (attitude == Attitude.AGGRESSIVE) {
      if (missingTechs.length == 1) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Electrics, level);
      }
    } else if (attitude == Attitude.MILITARISTIC) {
      if (!targetingComputerTech && !jammerTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Electrics, level);
      }
    } else if (attitude == Attitude.BACKSTABBING) {
      if (!cloakTech && !scannerTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Electrics, level);
      }
    } else if (attitude == Attitude.LOGICAL) {
      if (!planetaryScannerTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Electrics, level);
      }
    } else if (attitude == Attitude.EXPANSIONIST) {
      if (!longScannerTech) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Electrics, level);
      }
    } else if (attitude == Attitude.SCIENTIFIC) {
      if (DiceGenerator.getRandom(99) < 20) {
        level = level + 1;
        info.getTechList().setTechLevel(TechType.Electrics, level);
      }
    } else if (DiceGenerator.getRandom(99) < 30) {
      level = level + 1;
      info.getTechList().setTechLevel(TechType.Electrics, level);
    }
  }
  /**
   * Handle research for AI player
   * @param info PlayerInfo
   */
  public static void handle(final PlayerInfo info) {
    Tech[] techs = info.getTechList().getList();
    boolean basicLab = false;
    for (int i = 0; i < techs.length; i++) {
      if (techs[i].getName().equals("Basic lab")) {
        basicLab = true;
      }
    }
    if (!basicLab) {
      info.getTechList().setTechFocus(TechType.Improvements, 40);
    } else {
      switch (info.getRace()) {
      case HUMAN: {
        info.getTechList().setTechFocus(TechType.Combat,
            HIGH_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Defense, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Hulls, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Propulsion,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Improvements,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Electrics,
            DEFAULT_FOCUS_LEVEL);
        break;
      }
      case CENTAURS: {
        info.getTechList().setTechFocus(TechType.Combat, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Defense, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Propulsion,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Improvements,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Electrics,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Hulls, HIGH_FOCUS_LEVEL);
        break;
      }
      case GREYANS: {
        info.getTechList().setTechFocus(TechType.Combat, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Defense, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Hulls, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Propulsion,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Electrics,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Improvements,
            HIGH_FOCUS_LEVEL);
        break;
      }
      case SPORKS: {
        info.getTechList().setTechFocus(TechType.Improvements,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Electrics, LOW_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Propulsion, LOW_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Hulls,  HIGH_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Defense, HIGH_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Combat, HIGH_FOCUS_LEVEL);
        break;
      }
      case MECHIONS: {
        info.getTechList().setTechFocus(TechType.Defense, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Hulls, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Improvements,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Electrics, LOW_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Propulsion, HIGH_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Combat, HIGH_FOCUS_LEVEL);
        break;
      }
      case MOTHOIDS: {
        info.getTechList().setTechFocus(TechType.Defense, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Hulls, HIGH_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Improvements,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Electrics, LOW_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Propulsion, HIGH_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Combat, DEFAULT_FOCUS_LEVEL);
        break;
      }
      case TEUTHIDAES: {
        info.getTechList().setTechFocus(TechType.Defense, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Hulls, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Improvements,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Electrics, HIGH_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Propulsion, LOW_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Combat, HIGH_FOCUS_LEVEL);
        break;
      }
      case SCAURIANS: {
        info.getTechList().setTechFocus(TechType.Improvements,
            HIGH_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Defense, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Electrics, LOW_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Propulsion, HIGH_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Combat, LOW_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Hulls, HIGH_FOCUS_LEVEL);
        break;
      }
      default: {
        //HUMAN
        info.getTechList().setTechFocus(TechType.Combat,
            HIGH_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Defense, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Hulls, DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Propulsion,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Improvements,
            DEFAULT_FOCUS_LEVEL);
        info.getTechList().setTechFocus(TechType.Electrics,
            DEFAULT_FOCUS_LEVEL);
        break;
      }
      }
      Attitude attitude = info.getAiAttitude();
      if (info.getTechList().isUpgradeable(TechType.Combat)) {
        checkUpdateCombat(info, attitude);
      }
      if (info.getTechList().isUpgradeable(TechType.Defense)) {
        checkUpdateDefense(info, attitude);
      }
      if (info.getTechList().isUpgradeable(TechType.Hulls)) {
        checkUpdateHull(info, attitude);
      }
      if (info.getTechList().isUpgradeable(TechType.Improvements)) {
        checkUpdateImprovement(info, attitude);
      }
      if (info.getTechList().isUpgradeable(TechType.Propulsion)) {
        checkUpdatePropulsion(info, attitude);
      }
      if (info.getTechList().isUpgradeable(TechType.Electrics)) {
        checkUpdateElectronics(info, attitude);
      }
    }
  }
}
