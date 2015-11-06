package org.molgenis.selenium.model.component;

import java.util.List;

public interface MultiSelectModel
{

	void clearSelection();

	List<String> getSelectedLabels();

	void select(String... terms) throws InterruptedException;

}
