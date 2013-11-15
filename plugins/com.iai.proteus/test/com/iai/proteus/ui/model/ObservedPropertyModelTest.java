package com.iai.proteus.ui.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iai.proteus.queryset.Category;

/**
 * Unit tests for observed property model
 *  
 * @author b0kaj
 *
 */
public class ObservedPropertyModelTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void observedPropertyModelTest() {
		
		ObservedPropertyModel model = new ObservedPropertyModel();

		// only has default category 
		assertThat(model.getCategories().size(), is(1));
		
		// add property 
		model.addObservedProperty("http://mmisw.org/ont/cf/parameter/sea_water_temperature");
		
		// new default category should have been added 
		assertThat(model.getCategories().size(), is(2));
		
		Category[] categories = 
				(Category[])model.getCategories().toArray(new Category[0]);
		assertThat(categories[0].getName(), is(ObservedPropertyModel.categoryOtherName));
		assertThat(categories[1].getName(), is("Climate"));
		
		assertThat(categories[1].getObservedProperties().size(), is(1));
		for (ObservedProperty p : categories[1].getObservedProperties()) {
			assertThat(p.isSelected(), is(false));
		}
		categories[1].select();
		for (ObservedProperty p : categories[1].getObservedProperties()) {
			assertThat(p.isSelected(), is(true));
		}
	}
	
	@Test
	public void addNewCategoryRuleTest() {
		ObservedPropertyModel model = new ObservedPropertyModel();

		model.addCategoryRule("Emo", "happy,fearful");
		
		// add property 
		model.addObservedProperty("http://emo.com/happy");

		// new default category should have been added 
		assertThat(model.getCategories().size(), is(2));
		
		Category[] categories = 
				(Category[])model.getCategories().toArray(new Category[0]);
		assertThat(categories[0].getName(), is(ObservedPropertyModel.categoryOtherName));
		assertThat(categories[1].getName(), is("Emo"));
	}

}
