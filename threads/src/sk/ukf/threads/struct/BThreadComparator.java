package sk.ukf.threads.struct;

import java.util.Comparator;

public class BThreadComparator implements Comparator<BThread> {

	/**
	 * Metoda porovna dva BThread objekty na zaklade priority a vrati vysledok.
	 * Cim nizsie cislo priority, tym vyssia priorita (napr. 1 ma vacsiu
	 * prioritu ako 2). - Ak ma o1 vyssiu prioritu ako o2, vracia -1. - Ak ma o1
	 * mensiu prioritu ako o2, vracia 1. - Ak ma o1 rovnaku prioritu ako o2,
	 * vracia 0.
	 */
	@Override
	public int compare(BThread o1, BThread o2) {

		if ((o1 == null) && (o2 == null)) {
			return 0;
		} else if ((o1 == null) && (o2 != null)) {
			return -1;
		} else if ((o1 != null) && (o2 == null)) {
			return 1;
		} else {
			// porovname na zaklade priority
			if (o1.getPriority() < o2.getPriority()) {
				// o1 ma vyssiu prioritu
				return -1;
			} else if (o1.getPriority() > o2.getPriority()) {
				// o1 ma nizsiu prioritu
				return 1;
			} else {
				// maju rovnaku prioritu
				return 0;
			}
		}
	}
}