package org.vaadin.mvm;

import org.vaadin.addon.formbinder.ViewBoundForm;
import org.vaadin.mvm.domain.Person;
import org.vaadin.mvm.domain.PlaceMark;
import org.vaadin.vol.Point;
import org.vaadin.vol.VectorLayer.VectorDrawnEvent;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class PlaceMarkEditor extends Popover implements ClickListener {

	private NavigationView c = new NavigationView();

	private Button close = new Button("✓", this);
	private Button delete = new Button("✗", this);

	private PlaceMark placeMark;

	private PhotoField photoField = new PhotoField();
	private TextField latField = new TextField("Lan");
	private TextField lonField = new TextField("Lon");
	private TextField nameField = new TextField("Name");
	private Label distance = new Label();

	private MainView master;

	public PlaceMarkEditor(MainView master, Component relativeComponent,
			PlaceMark pm) {
		this.master = master;
		placeMark = pm;
		buildView(relativeComponent);
	}

	public PlaceMarkEditor(MainView master, Component relativeComponent,
			VectorDrawnEvent event) {
		this.master = master;
		creatPlaceMarkFromVectorDrawnEvent(event);
		buildView(relativeComponent);
	}

	private void buildView(Component relativeComponent) {
		setClosable(false);
		setWidth("300px");
		setHeight("80%");
		c.setRightComponent(close);
		c.setLeftComponent(delete);
		c.setCaption("Placemark");
		setContent(c);
		CssLayout l = new CssLayout();


		VerticalComponentGroup verticalComponentGroup = new VerticalComponentGroup();
		verticalComponentGroup.addComponent(nameField);
		nameField.setWidth("100%");
		verticalComponentGroup.addComponent(lonField);
		lonField.setWidth("100%");
		verticalComponentGroup.addComponent(latField);
		latField.setWidth("100%");

		/* Let FormBinder bind our fields */;
		ViewBoundForm vbf = new ViewBoundForm();
		vbf.setCustomFieldSources(this);
		vbf.setItemDataSource(new BeanItem<PlaceMark>(placeMark));

		/* Calculate and add distance as "read only" value to view */
		Point myPlace = master.getLastPoints().get(
				master.getLastPoints().size() - 1);
		LatLng point1 = new LatLng(myPlace.getLat(), myPlace.getLon());
		LatLng point2 = new LatLng(placeMark.getLat(), placeMark.getLon());
		double distanceInKilometers = LatLngTool.distance(point1, point2,
				LengthUnit.KILOMETER);
		distance.setCaption("Distance");
		distance.setValue(String.format("%.2f km away", distanceInKilometers));
		verticalComponentGroup.addComponent(distance);

		verticalComponentGroup.addComponent(photoField);
		l.addComponent(verticalComponentGroup);
		
		c.setContent(l);
		showRelativeTo(relativeComponent);
	}

	private void creatPlaceMarkFromVectorDrawnEvent(VectorDrawnEvent event) {
		placeMark = new PlaceMark();
		Person user = (Person) MobileVaadinMaps.get().getUser();
		user.getPlaceMarks().add(placeMark);
		Point point = event.getVector().getPoints()[0];
		placeMark.setPoint(point);
	}

	public void buttonClick(ClickEvent event) {
		if(event.getButton() == delete) {
			master.removeDisplayedPlaceMark(placeMark);
			master.getUser().getPlaceMarks().remove(placeMark);
		} else if (event.getButton() == close) {
			master.addDisplayedPlaceMark(placeMark);
		}
		getParent().removeWindow(this);
	}

}
