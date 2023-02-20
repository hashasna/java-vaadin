package com.example.application.views.list;

import com.example.application.data.entity.Contact;
import com.example.application.data.service.CrmService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.management.Notification;

import java.util.Collections;

import static com.vaadin.flow.component.notification.Notification.show;

//@PageTitle("list")
@PageTitle("Contact | Vaadin CRM")
@Route(value = "", layout = MainLayout.class)
public class ListView extends VerticalLayout {


    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterText = new TextField();
    ContactForm form;
    private CrmService service;

    public ListView(CrmService service) {
//        setSpacing(false);
//
//        Image img = new Image("images/empty-plant.png", "placeholder plant");
//        img.setWidth("200px");
//        add(img);
//
//        add(new H2("This place intentionally left empty"));
//        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
//
//        setSizeFull();
//        setJustifyContentMode(JustifyContentMode.CENTER);
//        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
//        getStyle().set("text-align", "center");

//        Button button = new Button("Click Me");
//        TextField name = new TextField("Name :");
//
//        HorizontalLayout hl = new HorizontalLayout(name, button);
//        hl.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
//
//        button.addClickListener(click-> show("Hello, " + name.getValue()));
//
//        add(hl);

        this.service = service;
        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureForm();

        add(
                getToolbar(),
                getContent()
        );

        updateList();
        closeEditor();
    }

    public void closeEditor(){
        form.setContact(null);
        form.setVisible(false);
        removeClassName("Editing");
    }

    private void updateList() {
        grid.setItems(service.findAllContacts(filterText.getValue()));
    }

    private Component getContent(){
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private void configureForm(){
        form = new ContactForm(service.findAllCompanies(), service.findAllStatuses());
        form.setWidth("25em");
        
        form.addListener(ContactForm.SaveEvent.class, this::saveContact);
        form.addListener(ContactForm.DeleteEvent.class, this::deleteContact);

    }

   private void saveContact(ContactForm.SaveEvent event){
        service.saveContact(event.getContact());
        updateList();
        closeEditor();
   }

    private void deleteContact(ContactForm.DeleteEvent event){
        service.deleteContact(event.getContact());
        updateList();
        closeEditor();
    }

    private void configureGrid(){
        grid.addClassName("Contact-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email");
        grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Status");
        grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Company");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(e -> editContact(e.getValue()));

    }

    private void editContact(Contact contact) {
        if(contact == null){
            closeEditor();
        }else {
            form.setContact(contact);
            form.setVisible(true);
            addClassName("Editing");

        }
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by Name ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add Contact");
        addContactButton.addClickListener(e -> addContact());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Contact());
    }

}
