package org.openRealmOfStars.AI.Mission;

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
 * Mission for AI
 *
 */
public final class Mission {

  /**
   * Mission type
   */
  private MissionType type;

  /**
   * Mission phase
   */
  private MissionPhase phase;

  /**
   * Target X coordinate
   */
  private int targetXCoordinate;
  /**
   * Target Y coordinate
   */
  private int targetYCoordinate;

  /**
   * Fleet name which is on the mission
   */
  private String fleetName;

  /**
   * Planet name which is building the ship
   */
  private String planetBuilding;

  /**
   * Planet name which is going to be conquered;
   */
  private String targetPlanet;

  /**
   * Solar system name where to go to explore
   */
  private String sunName;

  /**
   * How many turns mission has been on some fleet.
   * Usually this calculates execution time. It does not tell how many turns
   * ago mission was created. Mission time can be used to decide when
   * to upgrade defending fleet, when explorer should move to next solar system.
   * Each mission will have their own way of calculating this one.
   */
  private int missionTime;

  /**
   * Create new mission for AI
   * @param missionType MissionType
   * @param missionPhase Mission Phase
   * @param tx Target X
   * @param ty Target Y
   */
  public Mission(final MissionType missionType, final MissionPhase missionPhase,
      final int tx, final int ty) {
    this.type = missionType;
    this.phase = missionPhase;
    setMissionTime(0);
    setTarget(tx, ty);
    targetPlanet = null;
  }

  /**
   * Set the target's coordinate.
   *
   * @param x Target X coordinate
   * @param y Target Y coordinate
   */
  public void setTarget(final int x, final int y) {
    this.targetXCoordinate = x;
    this.targetYCoordinate = y;
  }

  /**
   * Return the mission type.
   *
   * @return the mission type
   */
  public MissionType getType() {
    return type;
  }

  /**
   * Set the mission type.
   *
   * @param missionType the mission type to set
   */
  public void setType(final MissionType missionType) {
    this.type = missionType;
  }

  public MissionPhase getPhase() {
    return phase;
  }

  public void setPhase(final MissionPhase missionPhase) {
    this.phase = missionPhase;
  }

  public String getFleetName() {
    return fleetName;
  }

  public void setFleetName(final String name) {
    this.fleetName = name;
  }

  public String getPlanetBuilding() {
    return planetBuilding;
  }

  public void setPlanetBuilding(final String building) {
    this.planetBuilding = building;
  }

  public int getX() {
    return targetXCoordinate;
  }

  public int getY() {
    return targetYCoordinate;
  }

  @Override
  public String toString() {
    return getType().toString() + " - " + getPhase().toString() + "\nPlanet:"
        + getPlanetBuilding() + "\nFleet:" + getFleetName() + "\nSolar:"
        + getSunName();
  }

  public String getSunName() {
    return sunName;
  }

  public void setSunName(final String name) {
    this.sunName = name;
  }

  public int getMissionTime() {
    return missionTime;
  }

  public void setMissionTime(final int time) {
    this.missionTime = time;
  }

  public String getTargetPlanet() {
    return targetPlanet;
  }

  public void setTargetPlanet(final String target) {
    this.targetPlanet = target;
  }

}
