package sk.ukf.threads.struct;

/**
 * @author zimanyiti Vseobecna trieda implementujuca java vlakno(interface
 *         Runnable). Obsahuje: - ID vlakna - cez metody setID, getID - Meno
 *         vlakna - cez metody seName, getName, alebo cez konstruktor - Priorita
 *         vlakna - cez metody setPriority, getPriority, alebo cez konstruktor -
 *         Zaradenie do vystupnej mapy - cez metody setDropAfterFinish,
 *         isDropAfterFinish, alebo cez konstruktor Popis fungovania: Vlakno od
 *         svojho vytvorenia a nastartovania absolvuje cyklus spracovania
 *         pomocou manazera vlakien(ak je priradeny). Zavolanim metody
 *         startThread sa vlakno priradi do spravovania manazera vlakien, ktory
 *         mu prideli unikatne ID, ktore vrati metoda startThread. Manazer
 *         vlakien ma urcite maximalne povolene mnozstvo prave beziacich
 *         vlakien. Mozu nastat 2 situacie: - mnozstvo aktualne beziacich
 *         vlakien nepresahuje maximalnu povolenu hodnotu: Vlakno sa spusti a po
 *         svojom skonceni sa bud zaradi do mapy skoncenych vlakien, alebo sa
 *         uvolni(podla atributu dropAfterFinished). - mnozstvo aktualne
 *         beziacich vlakien presahuje maximalnu povolenu hodnotu: Vlakno sa
 *         zaradi do fronty cakajucich na zaklade svojej priority. Ked sa
 *         dostane na rad, spusti sa a po svojom ukonceni sa bud zaradi do mapy
 *         skoncenych vlakien, alebo sa uvolni(podla atributu
 *         dropAfterFinished). Ak nie je priradeny manazer vlakien, sprava sa
 *         vlakno ako klasicky vlakno. Zavolanim metody startProcessing sa
 *         spusti, stopProcessing zastavi.
 */
public abstract class BThread implements Runnable {

	// ID pre manazera vlakien - POZOR!! - priradzuje si manazer vlakien a
	// vracia ako navratovu hodnotu!!
	protected volatile Long ID = 0L;

	// meno vlakna
	protected String name;
	// popis vlakna
	protected String popis;

	// priorita vlakna - cim nizsia hodnota, tym vyssia priorita - pre
	// BThreadComparator
	protected Integer priority = 0;

	// premenna urcena na pouzivanie ovladania hlavneho tela cyklu metody run
	protected volatile boolean threadRunning = false;

	// premenna urcujuca, ci sa ma vlakno po skonceni zaradit do mapy ukoncenych
	// vlakien
	protected boolean dropAfterFinished = false;

	// samotne vlakno
	protected Thread thread;

	// Manazer vlakien spravujuci toto vlakno
	protected BThreadManager parentManager;

	// premenna hovoriaca o tom, ci uz bolo vlano zaradeny do spracovacieho
	// procesu threadManagera.
	protected volatile boolean assignedToManager = false;
	// stav vlakna pocas spracovania - obsluhuje manazer vlakien
	protected volatile int threadState = BThreadConstants.THREAD_STATE_UNDEFINED;

	// ak sa vyhodi vynimka, tak tato premenna obsahuje jej spravu
	protected volatile String exceptionMessage = null;

	/**
	 * TUTO METODU VLAKNA NEOVERRIDOVAT!!!!! OVERRIDOVAT ABSTRAKTNU METODU
	 * BThreadRun!!
	 */
	public void run() {
		threadRunning = true;
		try {
			bThreadRun();
		} catch (Exception ex) {
			exceptionMessage = ex.getMessage();
		} finally {
			threadRunning = false;
			thread = null;
			// ak je vlakno zaradene do manazera vlakien, tak podla nastavenia,
			// ho zaradime bud do mapy dokoncenych vlakien, alebo ho vyhodime
			// uplne
			if (parentManager != null) {
				parentManager.removeFromThreadProcessing(getID());
			}
		}
	}

	protected abstract void bThreadRun() throws Exception;

	/**
	 * Konstruktor, ktori len vytvori vlakno, no nenastavi mu ID ani meno.
	 */
	public BThread() {
		super();
		this.name = null;
		this.popis = null;
		this.priority = 0;
		this.dropAfterFinished = true;
		this.parentManager = null;
	}

	public BThread(String name, String popis, BThreadManager parentManager) {
		super();
		this.name = name;
		this.popis = popis;
		this.priority = 0;
		this.dropAfterFinished = true;
		this.parentManager = parentManager;
	}

	public BThread(String name, String popis, Integer priority,
			BThreadManager parentManager) {
		super();
		this.name = name;
		this.popis = popis;
		this.priority = priority;
		this.dropAfterFinished = true;
		this.parentManager = parentManager;
	}

	public BThread(String name, String popis, Integer priority,
			boolean dropAfterFinished, BThreadManager parentManager) {
		super();
		this.name = name;
		this.popis = popis;
		this.priority = priority;
		this.dropAfterFinished = dropAfterFinished;
		this.parentManager = parentManager;
	}

	public void startThread() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		} else {
			// TODO tu by sa hodil nejaky logger. Trebars Log4j.
		}
	}

	/**
	 * Metoda zaradi vlakno do "zivotneho cyklu" v manazeri vlakien, alebo ho
	 * pusti.. Podla nastavenia dropAfterFinished
	 */
	public Long startProcessing() {
		// ak mame nejakeho manazera vlakien
		if (parentManager != null) {
			// ak je uz zaradeny do procesu manazera vlakien, upozornime na to
			// a vratime jeho id
			if (!assignedToManager) {
				return parentManager.assignToThreadProcessing(this);
			} else {
				// TODO tu by sa hodil nejaky logger. Trebars Log4j.
				return getID();
			}
		} else {
			if (thread == null) {
				thread = new Thread(this);
				thread.start();
			} else {
				// TODO tu by sa hodil nejaky logger. Trebars Log4j.
			}

			// idcko sa nepriradzuje
			return 0L;
		}
	}

	/**
	 * Metoda zastavi spracovanie vlakna
	 */
	public void stopProcessing() {
		threadRunning = false;
		if (thread != null) {
			thread.interrupt();
			thread = null;
		} else {
			// TODO tu by sa hodil nejaky logger. Trebars Log4j.
		}

	}

	/**
	 * Metoda vrati ID vlakna.
	 * 
	 * @return
	 */
	public Long getID() {
		return ID;
	}

	/**
	 * Metoda priradi ID vlaknu.
	 * 
	 * @param threadID
	 */
	public void setID(Long ID) {
		this.ID = ID;
	}

	public void setThreadRunning(boolean value) {
		threadRunning = value;
	}

	public boolean isThreadRunning() {
		return threadRunning;
	}

	/**
	 * Metoda vrati prioritu vlakna.
	 * 
	 * @return
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * Metoda nastavi prioritu vlaknu.
	 * 
	 * @param priority
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * Metoda vrati meno vlakna.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Metoda nastavi meno vlakna.
	 * 
	 * @param threadName
	 */
	public void setName(String Name) {
		this.name = Name;
	}

	public boolean isDropAfterFinished() {
		return dropAfterFinished;
	}

	public void setDropAfterFinished(boolean dropAfterFinished) {
		this.dropAfterFinished = dropAfterFinished;
	}

	public void setAssignedToManager() {
		assignedToManager = true;
	}

	public String getPopis() {
		return popis;
	}

	public void setPopis(String popis) {
		this.popis = popis;
	}

	public boolean isAssignedToManager() {
		return assignedToManager;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public int getThreadState() {
		return threadState;
	}

	public void setThreadState(int threadState) {
		this.threadState = threadState;
	}
}