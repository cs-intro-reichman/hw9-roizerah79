/**
 * Represents a managed memory space. The memory space manages a list of allocated 
 * memory blocks, and a list free memory blocks. The methods "malloc" and "free" are 
 * used, respectively, for creating new blocks and recycling existing blocks.
 */
public class MemorySpace {
	
	// A list of the memory blocks that are presently allocated
	private LinkedList allocatedList;

	// A list of memory blocks that are presently free
	private LinkedList freeList;

	/**
	 * Constructs a new managed memory space of a given maximal size.
	 * 
	 * @param maxSize
	 *            the size of the memory space to be managed
	 */
	public MemorySpace(int maxSize) {
		// initiallizes an empty list of allocated blocks.
		allocatedList = new LinkedList();
	    // Initializes a free list containing a single block which represents
	    // the entire memory. The base address of this single initial block is
	    // zero, and its length is the given memory size.
		freeList = new LinkedList();
		freeList.addLast(new MemoryBlock(0, maxSize));
	}

	/**
	 * Allocates a memory block of a requested length (in words). Returns the
	 * base address of the allocated block, or -1 if unable to allocate.
	 * 
	 * This implementation scans the freeList, looking for the first free memory block 
	 * whose length equals at least the given length. If such a block is found, the method 
	 * performs the following operations:
	 * 
	 * (1) A new memory block is constructed. The base address of the new block is set to
	 * the base address of the found free block. The length of the new block is set to the value 
	 * of the method's length parameter.
	 * 
	 * (2) The new memory block is appended to the end of the allocatedList.
	 * 
	 * (3) The base address and the length of the found free block are updated, to reflect the allocation.
	 * For example, suppose that the requested block length is 17, and suppose that the base
	 * address and length of the the found free block are 250 and 20, respectively.
	 * In such a case, the base address and length of of the allocated block
	 * are set to 250 and 17, respectively, and the base address and length
	 * of the found free block are set to 267 and 3, respectively.
	 * 
	 * (4) The new memory block is returned.
	 * 
	 * If the length of the found block is exactly the same as the requested length, 
	 * then the found block is removed from the freeList and appended to the allocatedList.
	 * 
	 * @param length
	 *        the length (in words) of the memory block that has to be allocated
	 * @return the base address of the allocated block, or -1 if unable to allocate
	 */
	public int malloc(int length) {		
		ListIterator finder = this.freeList.iterator();
		for(int i=0;i<this.freeList.getSize();i++){
			MemoryBlock potblock = finder.next();
			if(potblock.length<length){
				continue;
			}
			MemoryBlock block = new MemoryBlock(potblock.baseAddress,length);
			this.allocatedList.addLast(block);
			if(potblock.length==length) this.freeList.remove(potblock);
			else{
				potblock.length -= length;
				potblock.baseAddress += length;
			}
			return block.baseAddress;
		}
		return -1;
	}

	/**
	 * Frees the memory block whose base address equals the given address.
	 * This implementation deletes the block whose base address equals the given 
	 * address from the allocatedList, and adds it at the end of the free list. 
	 * 
	 * @param baseAddress
	 *            the starting address of the block to freeList
	 */
	public void free(int address) { 
		if(this.allocatedList.getSize()==0){
			throw new IllegalArgumentException(
					"index must be between 0 and size");
		}
		ListIterator finder = this.allocatedList.iterator();
		for(int i=0;i<this.allocatedList.getSize();i++){
			if(finder.current.block.baseAddress==address){
				this.freeList.addLast(finder.current.block);
				this.allocatedList.remove(finder.current.block);
			}
			finder.next();
		}	
	}
	
	
	/**
	 * A textual representation of the free list and the allocated list of this memory space, 
	 * for debugging purposes.
	 */
	public String toString() {
		return freeList.toString() + "\n" + allocatedList.toString();		
	}
	
	/**
	 * Performs defragmantation of this memory space.
	 * Normally, called by malloc, when it fails to find a memory block of the requested size.
	 * In this implementation Malloc does not call defrag.
	 */
	public void defrag() {
		ListIterator finder = this.freeList.iterator();
		while (finder.hasNext()) {
			boolean defraged = false;
			int checker = finder.current.block.baseAddress+finder.current.block.length;
			ListIterator scouter = this.freeList.iterator();
			scouter.next();
			while(scouter.hasNext()){
				if(checker==scouter.current.block.baseAddress){
					MemoryBlock block = new MemoryBlock(finder.current.block.baseAddress, (scouter.current.block.length+finder.current.block.length));
					int index = this.freeList.indexOf(finder.current.block);
					this.freeList.remove(finder.current);
					this.freeList.remove(scouter.current);
					this.freeList.add(index, block);
					finder = this.freeList.iterator();
					defraged = true;
					break;
				}
				scouter.next();
			}
			if(!defraged) finder.next();
		}
	}
}
