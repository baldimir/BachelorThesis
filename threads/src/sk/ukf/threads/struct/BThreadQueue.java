package sk.ukf.threads.struct;

import java.util.PriorityQueue;

/**
 * @author zimanyiti - Prioritny FIFO zasobnik objektov BThread. Objekty BThread
 *         sa vkladaju do fronty na zaklade priority urcenej v prave vkladanom
 *         threade. Cim nizsie cislo priority, tym vyssia priorita.
 */

public class BThreadQueue extends PriorityQueue<BThread> {

	private static final long serialVersionUID = 1L;

	BThreadQueue() {
		super();
	}

	BThreadQueue(int initialCapacity, BThreadComparator bThreadComparator) {
		super(initialCapacity, bThreadComparator);
	}

	/**
	 * Metoda prida thread do fronty s urcitou prioritou.
	 * 
	 * @param dlThread
	 * @param priority
	 */
	public void addWithPriority(BThread bThread, Integer priority) {
		bThread.setPriority(priority);
		super.offer(bThread);
	}

	// mozno tu este nieco bude
}
