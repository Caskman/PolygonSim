package caskman.polygonsim.model;

import java.util.List;

import caskman.polygonsim.model.entities.Mob;

public class GameContext {
	public List<Mob> additions;
	public List<Mob> removals;
	public QuadTree quadTree;
}
