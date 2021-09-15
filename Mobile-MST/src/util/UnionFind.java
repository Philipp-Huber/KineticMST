package util;

public class UnionFind {
	
	int[] parents;
	int size;
	
	public UnionFind(int size) {
		initialize(size);
	}
	
	/**
	 * Initializes the union find data structure for a number of entities equal to the size of the pointset. Entities are initialized to -1.
	 */
	public void initialize(int size) {
		this.size = size;
		this.parents = new int[this.size];
		
		for (int i = 0; i < size; i++) {
			parents[i] = -1;
		}
	}
	
	/**
	 * Retrieves the group representative for an entity in a union find data structure
	 * @param parents A unit find data structure
	 * @param child Index of an entity in the unit find data structure
	 * @return Index of the entitie's group representative
	 */
	public int getRepresentative(int child) {
		if (parents[child] == -1) {
			return child;
		}
		return getRepresentative(parents[child]);
	}
	
	/**
	 * Adds a new relation between to entities in a unit find data structure
	 * @param parents A unit find data structure
	 * @param src The first entity for the added relation
	 * @param dest The second entity for the added relation
	 */
	public void union(int src, int dest) {
		int rep1 = getRepresentative(src);
		int rep2 = getRepresentative(dest);
		parents[rep1] = rep2;
	}
	
	public boolean inSameSet(int entry1, int entry2) {
		return (getRepresentative(entry1) == getRepresentative(entry2));
	}

}

