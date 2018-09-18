package vn.itt.msales.common;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class OptionItemComparator implements Comparator<OptionItem> {
	private Collator vnCollator=Collator.getInstance(new Locale("vi"));
	@Override
	public int compare(OptionItem o1, OptionItem o2) {
		if (o1.getName()==null) return -1;
		else if (o2.getName()==null) return 1;
		else return vnCollator.compare(o1.getName(), o2.getName());
	}
}
