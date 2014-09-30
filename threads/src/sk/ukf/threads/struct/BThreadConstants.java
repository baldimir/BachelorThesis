package sk.ukf.threads.struct;

/**
 * @author zimanyiti Trieda na udrziavanie konstant pre triedy spracovania
 *         vlakien.
 */
public class BThreadConstants {

	/** Maximalny pocet paralelne beziacich vlakien. */
	public static final int DEFAULT_MAX_RUNNING_THREADS_COUNT = 5;

	/** Stav vlakna - nedefinovany. */
	public static final int THREAD_STATE_UNDEFINED = 0;
	/** Stav vlakna - cakajuce na spustenie. */
	public static final int THREAD_STATE_WAITING_FOR_EXEC = 1;
	/** Stav vlakna - beziace. */
	public static final int THREAD_STATE_RUNNING = 2;
	/** Stav vlakna - skonceny beh. */
	public static final int THREAD_STATE_FINISHED = 3;
	/** Stav vlakna - skonceny beh - vynimka. */
	public static final int THREAD_STATE_EXCEPTION = 4;
}
