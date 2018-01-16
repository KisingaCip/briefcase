package org.opendatakit.briefcase.ui.export;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.opendatakit.briefcase.model.BriefcaseFormDefinition;
import org.opendatakit.briefcase.model.FormStatus;

public class ExportForms {
  private final List<FormStatus> forms;
  private final Map<FormStatus, ExportConfiguration> configurations = new HashMap<>();
  private Map<String, FormStatus> formsIndex = new HashMap<>();

  ExportForms() {
    forms = new ArrayList<>();
  }

  public ExportForms(List<FormStatus> forms) {
    this.forms = forms;
    rebuildIndex();
  }


  public void merge(List<FormStatus> forms) {
    this.forms.addAll(forms.stream().filter(form -> !formsIndex.containsKey(form.getFormDefinition().getFormId())).collect(toList()));
    rebuildIndex();
  }

  public int size() {
    return forms.size();
  }

  public FormStatus get(int rowIndex) {
    return forms.get(rowIndex);
  }

  public boolean hasConfiguration(FormStatus form) {
    return configurations.containsKey(form);
  }

  public ExportConfiguration getConfiguration(FormStatus form) {
    return configurations.computeIfAbsent(form, ___ -> ExportConfiguration.empty());
  }

  public Optional<ExportConfiguration> getConfiguration(BriefcaseFormDefinition formDefinition) {
    return Optional.ofNullable(configurations.get(getForm(formDefinition)));
  }

  public void removeConfiguration(FormStatus form) {
    configurations.remove(form);
  }

  public void setConfiguration(FormStatus form, ExportConfiguration configuration) {
    configurations.put(form, configuration);
  }

  public void selectAll() {
    forms.forEach(form -> form.setSelected(true));
  }

  public void clearAll() {
    forms.forEach(form -> form.setSelected(false));
  }

  public List<FormStatus> getSelectedForms() {
    return forms.stream().filter(FormStatus::isSelected).collect(toList());
  }

  public boolean someSelected() {
    return !getSelectedForms().isEmpty();
  }

  public boolean allSelected() {
    return forms.stream().allMatch(FormStatus::isSelected);
  }

  public boolean noneSelected() {
    return forms.stream().noneMatch(FormStatus::isSelected);
  }

  public boolean allSelectedFormsHaveConfiguration() {
    return getSelectedForms().stream().allMatch(form -> configurations.containsKey(form) && !configurations.get(form).isEmpty());
  }

  public void appendStatus(BriefcaseFormDefinition formDefinition, String statusUpdate, boolean successful) {
    getForm(formDefinition).setStatusString(statusUpdate, successful);
  }

  private FormStatus getForm(BriefcaseFormDefinition formDefinition) {
    return Optional.ofNullable(formsIndex.get(formDefinition.getFormId()))
        .orElseThrow(() -> new RuntimeException("Form " + formDefinition.getFormName() + " " + formDefinition.getFormId() + " not found"));
  }

  private void rebuildIndex() {
    formsIndex = forms.stream().collect(toMap(form -> form.getFormDefinition().getFormId(), form -> form));
  }
}