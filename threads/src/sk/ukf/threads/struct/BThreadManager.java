package sk.ukf.threads.struct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

// TODO - vymysliet nejake rozumne rozhranie manazera
public class BThreadManager {

	// pocitadlo vlakien - podla pocitadla sa prideluju IDcka vlaknam
	private Long threadCounter;

	// maximalne cislo moznych beziacich vlakien.
	// Ostatne vlakna su zaradene do FIFO fronty podla priority
	private Integer maxCountOfRunningThreads;

	private Map<Long, BThread> finishedThreadsMap;

	// mapa spracovavanych vlakien.. v tejto mape su LEN prave beziace vlakna
	private Map<Long, BThread> runningThreadsMap;

	// FIFO zasobnik vlakien cakajucich na spracovanie
	private BThreadQueue waitingThreadsQueue;

	public BThreadManager(Integer maxCountOfRunningThreads) {
		super();
		this.maxCountOfRunningThreads = maxCountOfRunningThreads;
		threadCounter = 0L;
		finishedThreadsMap = new HashMap<Long, BThread>();
		runningThreadsMap = new HashMap<Long, BThread>();
		waitingThreadsQueue = new BThreadQueue(11, new BThreadComparator());
	}

	protected void stopRunningThreads() {
		Set<Entry<Long, BThread>> runningThreadsEntrySet = runningThreadsMap
				.entrySet();
		for (Entry<Long, BThread> tempThread : runningThreadsEntrySet) {
			tempThread.getValue().stopProcessing();
		}
	}

	protected void incThreadCounter() {
		threadCounter = threadCounter + 1;
	}

	protected Long getThreadCounter() {
		return threadCounter;
	}

	protected Long getNextThreadCounter() {
		return (getThreadCounter() + 1);
	}

	public synchronized void removeFromThreadProcessing(Long threadID) {
		// najdeme si thread
		BThread tempThread = runningThreadsMap.get(threadID);

		if (tempThread == null) {
			// TODO - tu by sa hodil nejaky logger, napr. Log4j.
		} else {
			// zaradime do mapy dokoncenych threadov(ak sa ma zaradit)
			if (!tempThread.isDropAfterFinished()) {
				if (tempThread.getExceptionMessage() == null) {
					tempThread.setThreadState(BThreadConstants.THREAD_STATE_FINISHED);
				} else {
					tempThread.setThreadState(BThreadConstants.THREAD_STATE_EXCEPTION);
				}
				finishedThreadsMap.put(tempThread.getID(), tempThread);
			}

			// vyradime z beziacich threadov
			runningThreadsMap.remove(threadID);

			// ak mame nejaky cakajuci thread, tak ho spustime,
			// lebo sa uvolnilo miesto
			BThread queuedThread = waitingThreadsQueue.poll();

			if (queuedThread != null) {
				queuedThread.setThreadState(BThreadConstants.THREAD_STATE_RUNNING);
				runningThreadsMap.put(queuedThread.getID(), queuedThread);
				queuedThread.startThread();
			}
		}
	}

	/**
	 * Metoda zaradi bThread do procesu spracovania manazerom vlakien. Ak je
	 * volne miesto na spustenie vlakna, tak ho spusti, inak ho zaradi do
	 * fronty cakajucich vlakien na zaklade priority.
	 * 
	 * @param bThread
	 * @return - IDcko vlakna
	 */
	public synchronized Long assignToThreadProcessing(BThread bThread) {
		// ak je rozny od null
		if (bThread != null) {

			// priradime mu nove IDcko
			bThread.setID(getNextThreadCounter());

			// ak pocet beziacich vlakien nepresahuje maximalny pocet moznych
			// beziacich vlakien, tak ho zaradime ako beziaci a nastartujeme
			if (runningThreadsMap.size() < maxCountOfRunningThreads) {
				bThread.setThreadState(BThreadConstants.THREAD_STATE_RUNNING);
				runningThreadsMap.put(bThread.getID(), bThread);
				bThread.startThread();
			} else {
				// zaradime ho do fronty ako cakajuceho
				bThread.setThreadState(BThreadConstants.THREAD_STATE_WAITING_FOR_EXEC);
				waitingThreadsQueue.offer(bThread);
			}

			// nastavime ho ako "priradeny procesu spracovania"
			bThread.setAssignedToManager();

			// zvysime si threadCounter
			incThreadCounter();

			return bThread.getID();
		} else {
			throw new IllegalArgumentException("Assigned thread cannot be null!");
		}
	}

	private List<BThread> getThreadsWithException() {
		List<BThread> exceptionThreads = new ArrayList<BThread>();

		// zoberieme si skoncene vlakna a prejdeme ich..
		// ak je nejaky v stave vynimka, pridame ho do vysledku
		Collection<BThread> finishedThreads = finishedThreadsMap.values();
		for (BThread tempThread : finishedThreads) {
			if (tempThread.getThreadState() == BThreadConstants.THREAD_STATE_EXCEPTION) {
				exceptionThreads.add(tempThread);
			}
		}
		return exceptionThreads;
	}

	/**
	 * Metoda vrati zoznam vlakien na zaklade pozadovaneho typu.
	 * 
	 * @param threadType
	 * @return
	 */
	public List<BThread> getThreads(int threadState) {
		List<BThread> dlThreadList = new ArrayList<BThread>();

		switch (threadState) {
		case BThreadConstants.THREAD_STATE_UNDEFINED:
			dlThreadList.addAll(waitingThreadsQueue);
			dlThreadList.addAll(runningThreadsMap.values());
			dlThreadList.addAll(finishedThreadsMap.values());
			break;
		case BThreadConstants.THREAD_STATE_WAITING_FOR_EXEC:
			dlThreadList.addAll(waitingThreadsQueue);
			break;
		case BThreadConstants.THREAD_STATE_RUNNING:
			dlThreadList.addAll(runningThreadsMap.values());
			break;
		case BThreadConstants.THREAD_STATE_FINISHED:
			dlThreadList.addAll(finishedThreadsMap.values());
			break;
		case BThreadConstants.THREAD_STATE_EXCEPTION:
			dlThreadList.addAll(getThreadsWithException());
			break;
		default:
			throw new IllegalArgumentException("Unsupported thread state!");
		}

		return dlThreadList;
	}

	public Integer getMaxCountOfRunningThreads() {
		return maxCountOfRunningThreads;
	}

	public void setMaxCountOfRunningThreads(Integer maxCountOfRunningThreads) {
		this.maxCountOfRunningThreads = maxCountOfRunningThreads;
	}
}
