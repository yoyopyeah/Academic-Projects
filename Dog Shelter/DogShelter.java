package assignment3;

import java.lang.reflect.Array;
import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DogShelter implements Iterable<Dog> {
	public DogNode root;

	public DogShelter(Dog d) {
		this.root = new DogNode(d);
	}

	private DogShelter(DogNode dNode) {
		this.root = dNode;
	}


	// add a dog to the shelter
	public void shelter(Dog d) {
		if (root == null) 
			root = new DogNode(d);
		else
			root = root.shelter(d);
	}

	// removes the dog who has been at the shelter the longest
	public Dog adopt() {
		if (root == null)
			return null;

		Dog d = root.d;
		root =  root.adopt(d);
		return d;
	}
	
	// overload adopt to remove from the shelter a specific dog
	public void adopt(Dog d) {
		if (root != null)
			root = root.adopt(d);
	}


	// get the oldest dog in the shelter
	public Dog findOldest() {
		if (root == null)
			return null;
		
		return root.findOldest();
	}

	// get the youngest dog in the shelter
	public Dog findYoungest() {
		if (root == null)
			return null;
		
		return root.findYoungest();
	}
	
	// get dog with highest adoption priority with age within the range
	public Dog findDogToAdopt(int minAge, int maxAge) {
		return root.findDogToAdopt(minAge, maxAge);
	}

	// Returns the expected vet cost the shelter has to incur in the next numDays days
	public double budgetVetExpenses(int numDays) {
		if (root == null)
			return 0;
		
		return root.budgetVetExpenses(numDays);
	}
	
	// returns a list of list of Dogs. The dogs in the list at index 0 need to see the vet in the next week. 
	// The dogs in the list at index i need to see the vet in i weeks. 
	public ArrayList<ArrayList<Dog>> getVetSchedule() {
		if (root == null)
			return new ArrayList<ArrayList<Dog>>();
			
		return root.getVetSchedule();
	}


	public Iterator<Dog> iterator() {
		return new DogShelterIterator();
	}


	public class DogNode {
		public Dog d;
		public DogNode younger;
		public DogNode older;
		public DogNode parent;

		public DogNode(Dog d) {
			this.d = d;
			this.younger = null;
			this.older = null;
			this.parent = null;
		}

		public DogNode shelter (Dog d) {
            // ADD YOUR CODE HERE
			//called on root, return the (new) root
			//1. Add a new node to the tree in the leaf position where binary search determines a node for d should be inserted.
			//2. Fix the trees so that the properties of the max heap are maintained (i.e. the parent node must have higher adoption priority than its children)
			DogNode addedDog = addDog(d);
			DogNode ans = this.upHeaps(addedDog);
			if (ans.parent == null) {
				return ans;
			}
			return this;
		}
		
		private DogNode addDog(Dog nd) {
//			DogNode result = new DogNode(nd);
			DogNode cur = this;
			if (nd.compareTo(cur.d) > 0) {
				if (cur.older == null) {
					cur.older = new DogNode(nd);
					cur.older.parent = cur;
					return cur.older;
				} else {
					return cur.older.addDog(nd);
				}
			} else {
				if (cur.younger == null){
					cur.younger = new DogNode(nd);
					cur.younger.parent = cur;
					return cur.younger;
				} else {
					return cur.younger.addDog(nd);
				}
			}
//			return result;
		}
		
		private DogNode upHeaps(DogNode root) {
			//call on root, return (new) root
//			DogNode cur = root;
			if (root.parent != null) { //if not root
				if (root.parent.d.getDaysAtTheShelter() < root.d.getDaysAtTheShelter()) { //need upheap
					root = rotate(root);
					upHeaps(root);
				}
			}
			return root;
		}
		
		private DogNode rotate(DogNode added) {
//			if (added.parent == null) { //if added is the root
//				return added;
//			}
				/*
							4b                   2a
						   /  \                 /  \
						 2a    5c    -->      1d    4b
						/  \                       /  \
					  1d    3e                   3e    5c
				 */
			
			boolean isYounger = false;
			boolean isRoot = false; //grandparent
			if (added.parent.parent != null) {
				if (added.parent.parent.d.compareTo(added.parent.d) > 0) {
					isYounger = true;
				}
			} else {
				isRoot = true;
			}
			if (added.d.compareTo(added.parent.d) < 0) {  //added is a younger, right rotation
				added.parent.younger = added.older;
				if (added.older != null) {
					added.older.parent = added.parent;
				}
				added.older = added.parent;
				if (isRoot) {
					added.parent = null;
				} else {
					if (isYounger) {
						added.parent.parent.younger = added;
					} else {
						added.parent.parent.older = added;
					}
					added.parent = added.parent.parent;
				}
				added.older.parent = added;
			} else {  //added is the older, so left rotate
				added.parent.older = added.younger;
				if (added.younger != null) {
					added.younger.parent = added.parent;
				}
				added.younger = added.parent;
				if (isRoot) {
					added.parent = null;
				} else {
					if (isYounger) {
						added.parent.parent.younger = added;
					} else {
						added.parent.parent.older = added;
					}
					added.parent = added.parent.parent;
				}
				added.younger.parent = added;
			}
			return added;
		}

		
		public DogNode adopt(Dog d) {
            // called on the tree root, takes a Dog d and removes the key equals to d from the tree, returns the (new) root
			//1. remove the matched dog. Find the oldest dog in the left subtree, and replace the dog to be removed, then remove oldest from the left subtree.
			//2. Perform downheap to recover the heap property if needed
			
			DogNode root = this;
			DogNode toAdopt = this;
			while (!toAdopt.d.equals(d)) {
				if (d.getAge() < toAdopt.d.getAge()) {
					toAdopt = toAdopt.younger;
				} else {
					toAdopt = toAdopt.older;
				}
			}
			
			//remove toAdopt
			if (toAdopt.younger == null || toAdopt.older == null) {  //if toAdopt can be removed right away + no worries of downHeap
				root = remove(toAdopt);
				if (root != null) { //toAdopt was NOT a leaf
					if (root.parent == null) {  //toAdopt was a root
						return root;
					} else {  //toAdopt was neither a leaf nor root, so in the middle of the tree
						return this;
					}
				} else {  //toAdopt was a leaf + root
					return null;
				}
			} else {  //else if toAdopt has both the children
				//1. find youngest from left subtree, and switch
				//2. remove toAdopt
				//3. might need downheap with older
				DogNode oldest = toAdopt.younger;
				while (oldest.older != null) {
					oldest = oldest.older;
				}
				// swap???
				// ok imma just try only swiching the element and not the nodes and see what i have
				Dog copy = oldest.d;
				oldest.d = toAdopt.d;
				toAdopt.d = copy;
				
				remove(oldest);
				root = downHeaps(toAdopt);
			}
			//todo: write two helper functions, one remove(toAdopt), one downHeap(root)
			//if toAdopt does not have both the children, then we can just remove(), else need swap() then remove()
//			toAdopt = null;
			while (root.parent != null) {
				root = root.parent;
			}
            return root;
		}
		
		private DogNode remove(DogNode r) {
			//only called when r does NOT have both the children
			//takes a dognode r that needs to be removed, returns the dognode that took r's place
			DogNode replacement = r;
			if (r.younger == null && r.older == null) {  //if toAdopt is a leaf
				if (r.parent == null) {  // if toAdopt is the only node in the tree
					return null;
				} else {
					if (r.d.compareTo(r.parent.d) < 0) { //if toAdopt is a younger
						r.parent.younger = null;
					} else {
						r.parent.older = null;
					}
					r.parent = null;
				}
//				toAdopt = null;
			} else if (r.older == null) {  //if younger is NOT null, and older is null
				replacement = r.younger;
				r.younger.parent = r.parent;
				if (r.parent != null) {
					if (r.d.compareTo(r.parent.d) < 0) { //if toAdopt is a younger
						r.parent.younger = r.younger;
					} else {
						r.parent.older = r.younger;
					}
				}
			} else if (r.younger == null) {  //if older is NOT null, and younger is null
				replacement = r.older;
				r.older.parent = r.parent;
				if (r.parent != null) {
					if (r.d.compareTo(r.parent.d) < 0) { //if toAdopt is a younger
						r.parent.younger = r.older;
					} else {
						r.parent.older = r.older;
					}
				}
			}
			r = null;
			return replacement;
		}
		
		private DogNode downHeaps(DogNode root) {
			//select which one of the child node has a higher priority and rotate with that
			//take input the node that might need downheap, return that node;
			DogNode target = root;
			
			if (target.older == null && target.younger == null) { //target is at leaf
				return target;
			} else if (target.older == null) {  //there is a younger but NO older
				if (target.d.getDaysAtTheShelter() < target.younger.d.getDaysAtTheShelter()) {
					target = rotate(target.younger).older;  //right rotate
					downHeaps(target);
				}
			} else if (target.younger == null) {  //there is an older but NO younger
				if (target.d.getDaysAtTheShelter() < target.older.d.getDaysAtTheShelter()) {
					target = rotate(target.older).younger;  //left rotate
					downHeaps(target);
				}
			} else {  //both the children are there, so rotate with the child with higher priority
				if (target.d.getDaysAtTheShelter() < target.younger.d.getDaysAtTheShelter() || target.d.getDaysAtTheShelter() < target.older.d.getDaysAtTheShelter()) {  //if downheap is needed
					if (target.older.d.getDaysAtTheShelter() > target.younger.d.getDaysAtTheShelter()) {
						//older has higher priority
						target = rotate(target.older).younger;  //left rotate
						downHeaps(target);
					} else {
						//younger has higher priority
						target = rotate(target.younger).older;  //right rotate
						downHeaps(target);
					}
				}
			}
			return target;
		}

		public Dog findOldest() {
            // ADD YOUR CODE HERE
			Dog ans = this.d;
			DogNode cur = this;
			if (cur.older != null) {
				ans = cur.older.findOldest();
			}
            return ans; // DON'T FORGET TO MODIFY THE RETURN IF NEED BE
		}

		public Dog findYoungest() {
            // ADD YOUR CODE HERE
			Dog ans = this.d;
			DogNode cur = this;
			if (cur.younger != null) {
				ans = cur.younger.findYoungest();
			}
			return ans;
		}

		public Dog findDogToAdopt(int minAge, int maxAge) {
            // ADD YOUR CODE HERE
			
			Dog ans = this.d;
			DogNode cur = this;
			if (ans.getAge() > maxAge) {
				cur = cur.younger;
				if (cur == null) {
					return null;
				}
				ans = cur.findDogToAdopt(minAge, maxAge);
			} else if (ans.getAge() < minAge) {
				cur = cur.older;
				if (cur == null) {
					return null;
				}
				ans = cur.findDogToAdopt(minAge, maxAge);
			}
            return ans;
		}
		
		public double budgetVetExpenses(int numDays) {
            // returns the expected amount of dollars that the shelter will need in the next numDays
			
			double ans = 0.0;
			for (Dog d : DogShelter.this) {
				if (d.getDaysToNextVetAppointment() <= numDays) {
					ans = ans + d.getExpectedVetCost();
				}
			}
            return ans; // DON'T FORGET TO MODIFY THE RETURN IF NEED BE
		}

		public ArrayList<ArrayList<Dog>> getVetSchedule() {
			//use iterator
			ArrayList<ArrayList<Dog>> schedule = new ArrayList<>();
			for (Dog d : DogShelter.this) {
				int week = d.getDaysToNextVetAppointment() / 7;
				while (schedule.size() <= week) {
					schedule.add(new ArrayList<Dog>());
				}
				schedule.get(week).add(d);
			}
            return schedule; // DON'T FORGET TO MODIFY THE RETURN IF NEED BE
		}

		public String toString() {
			String result = this.d.toString() + "\n";
			if (this.younger != null) {
				result += "younger than " + this.d.toString() + " :\n";
				result += this.younger.toString();
			}
			if (this.older != null) {
				result += "older than " + this.d.toString() + " :\n";
				result += this.older.toString();
			}
			/*if (this.parent != null) {
				result += "parent of " + this.d.toString() + " :\n";
				result += this.parent.d.toString() +"\n";
			}*/
			return result;
		}
		void print2DUtil(DogNode root, int space, int COUNT)
		{
			if (root == null)
				return;
			space += COUNT;
			print2DUtil(root.older, space,COUNT);
			System.out.print("\n");
			for (int i = COUNT; i < space; i++)
				System.out.print(" ");
			System.out.print(root.d + "\n");

			print2DUtil(root.younger , space, COUNT);
		}
		void print2D(DogNode root, int COUNT)
		{
			print2DUtil(root, 0, COUNT);
		}
		void print2D()
		{
			int COUNT = 20;
			print2DUtil(this, 0, COUNT);
		}
		
	}


	private class DogShelterIterator implements Iterator<Dog> {
		// HERE YOU CAN ADD THE FIELDS YOU NEED
		ArrayList<Dog> AList = new ArrayList<>();  //arraylist of the dogs in the shelter
		Dog cur;  //current dog the iterator is pointing to
		int pos;  //current iterator position
		
		//constructor
		private DogShelterIterator() {
			//YOUR CODE GOES HERE
			AList = makeList(DogShelter.this.root);  //initialize AList that we will traverse through
			cur = AList.get(0);  //initialize field cur
			pos = 0;  //initialize field pos
		}
		
		private ArrayList<Dog> makeList(DogNode root) {
			//inorderBT traversal
//			ArrayList<Dog> list = new ArrayList<>();
			if (root != null) {
				makeList(root.younger);
				AList.add(root.d);
				makeList(root.older);
			}
			return AList;
		}
		
		public Dog next(){
            //YOUR CODE GOES HERE
			Dog temp = cur;
			if (cur == null) {
				throw new NoSuchElementException();
			}
			pos = pos + 1;
			if (pos >= AList.size()) { //index out of bounds
				cur = null;
			} else {
				cur = AList.get(pos);
			}
			return temp;
			// DON'T FORGET TO MODIFY THE RETURN IF NEED BE
		}
//		public E next()
//		{
//			SNode<E> tmp = cur;
//			cur = cur.next;
//			return tmp.element;
//		}

		public boolean hasNext() {
            //YOUR CODE GOES HERE
			return (cur != null);
            // DON'T FORGET TO MODIFY THE RETURN IF NEED BE
		}

	}

}
