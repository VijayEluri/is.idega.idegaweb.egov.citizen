package is.idega.idegaweb.egov.citizen.presentation;

import is.idega.idegaweb.egov.business.UserInfoToExternalSystemService;
import is.idega.idegaweb.egov.citizen.IWBundleStarter;
import is.idega.idegaweb.egov.citizen.business.CitizenAccountSession;
import is.idega.idegaweb.egov.message.business.MessageSession;

import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.FinderException;
import javax.faces.component.UIComponent;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.data.ICRole;
import com.idega.core.builder.data.ICPage;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.file.data.ICFile;
import com.idega.core.localisation.presentation.LocalePresentationUtil;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.UploadFile;
import com.idega.presentation.CSSSpacer;
import com.idega.presentation.ExceptionWrapper;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.CountryDropdownMenu;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.FileInput;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.RadioGroup;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Gender;
import com.idega.user.data.GenderHome;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.EmailValidator;
import com.idega.util.FileUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

/*
 * import com.idega.presentation.ExceptionWrapper; import com.idega.presentation.IWContext; import com.idega.presentation.*; import
 * com.idega.presentation.ui.*; import com.idega.core.data.Address; import com.idega.core.data.Email; import com.idega.user.data.*; import
 * com.idega.business.IBOLookup; import com.idega.user.business.UserBusiness;
 *
 * import se.idega.idegaweb.commune.presentation.CommuneBlock;
 */
/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Anders Lindman
 * @version 1.0
 */
public class CitizenAccountPreferences extends CitizenBlock {

	private final static int ACTION_VIEW_FORM = 1;
	private final static int ACTION_FORM_SUBMIT = 2;

	private final static String PARAMETER_FORM_SUBMIT = "cap_sbmt";
	private final static String PARAMETER_EMAIL = "cap_email";
	private final static String PARAMETER_PHONE_HOME = "cap_phn_h";
	private final static String PARAMETER_PHONE_WORK = "cap_phn_w";
	private final static String PARAMETER_PHONE_MOBILE = "cap_phn_m";
	private final static String PARAMETER_CO_ADDRESS_SELECT = "cap_co";
	private final static String PARAMETER_CO_STREET_ADDRESS = "cap_co_sa";
	private final static String PARAMETER_CO_POSTAL_CODE = "cap_co_pc";
	private final static String PARAMETER_CO_CITY = "cap_co_ct";
	private final static String PARAMETER_CO_COUNTRY = "cap_co_country";
	private final static String PARAMETER_MESSAGES_VIA_EMAIL = "cap_m_v_e";
	private final static String PARAMETER_REMOVE_IMAGE = "cap_remove_image";
	private final static String PARAMETER_PREFERRED_LOCALE = "cap_pref_locale";
	private final static String PARAMETER_PREFERRED_ROLE = "cap_pref_role";
	protected final static String PARAMETER_NAME = "cap_name";
	protected final static String PARAMETER_SSN = "cap_ssn";
	private final static String PARAMETER_GENDER = "cap_gender";

	private final static String KEY_PREFIX = "citizen.";
	private final static String KEY_EMAIL = KEY_PREFIX + "email";
	private final static String KEY_UPDATE = KEY_PREFIX + "update";
	private final static String KEY_PHONE_HOME = KEY_PREFIX + "phone_home";
	private final static String KEY_PHONE_MOBILE = KEY_PREFIX + "phone_mobile";
	private final static String KEY_PHONE_WORK = KEY_PREFIX + "phone_work";
	private final static String KEY_CO_ADDRESS_SELECT = KEY_PREFIX + "co_address_select";
	private final static String KEY_CO_STREET_ADDRESS = KEY_PREFIX + "co_street_address";
	private final static String KEY_CO_POSTAL_CODE = KEY_PREFIX + "co_postal_code";
	private final static String KEY_CO_CITY = KEY_PREFIX + "co_city";
	private final static String KEY_CO_COUNTRY = KEY_PREFIX + "co_country";
	private final static String KEY_MESSAGES_VIA_EMAIL = KEY_PREFIX + "messages_via_email";
	private final static String KEY_EMAIL_INVALID = KEY_PREFIX + "email_invalid";
	private final static String KEY_CO_STREET_ADDRESS_MISSING = KEY_PREFIX + "co_street_address_missing";
	private final static String KEY_CO_POSTAL_CODE_MISSING = KEY_PREFIX + "co_postal_code_missing";
	private final static String KEY_CO_CITY_MISSING = KEY_PREFIX + "co_city_missing";
	private final static String KEY_PREFERENCES_SAVED = KEY_PREFIX + "preferenced_saved";
	private final static String KEY_NO_EMAIL_FOR_LETTERS = KEY_PREFIX + "no_email_to_send_letter_to";
	private final static String PREFERRED_LANGUAGE = "preferred_language";
	private final static String PREFERRED_ROLE = "preferred_role";

	private final static String DEFAULT_EMAIL = "E-mail";
	private final static String DEFAULT_UPDATE = "Update";
	private final static String DEFAULT_PHONE_HOME = "Phone (home)";
	private final static String DEFAULT_PHONE_MOBILE = "Phone (mobile)";
	private final static String DEFAULT_PHONE_WORK = "Phone (work)";
	private final static String DEFAULT_CO_ADDRESS_SELECT = "Use c/o address";
	private final static String DEFAULT_CO_STREET_ADDRESS = "Street address c/o";
	private final static String DEFAULT_CO_POSTAL_CODE = "Postal code c/o";
	private final static String DEFAULT_CO_CITY = "City c/o";
	private final static String DEFAULT_CO_COUNTRY = "Country c/o";
	private final static String DEFAULT_MESSAGES_VIA_EMAIL = "I want to get my messages via e-mail";
	private final static String DEFAULT_EMAIL_INVALID = "Email address invalid.";
	private final static String DEFAULT_CO_STREET_ADDRESS_MISSING = "Street address c/o must be entered.";
	private final static String DEFAULT_CO_POSTAL_CODE_MISSING = "Postal code c/o must be entered.";
	private final static String DEFAULT_CO_CITY_MISSING = "City c/o must be entered.";
	private final static String DEFAULT_PREFERENCES_SAVED = "Your preferences has been saved.";
	private final static String DEFAULT_NO_EMAIL_FOR_LETTERS = "No email entered to send letters to.";
	public static final String CITIZEN_ACCOUNT_PREFERENCES_PROPERTIES = "citizen_account_preferences";
	public static final String USER_PROPERTY_USE_CO_ADDRESS = "cap_use_co_address";

	protected User user = null;

	protected IWResourceBundle iwrb;

	private boolean showPreferredLocaleChooser = false;

	private boolean showPreferredRoleChooser = true;

	private boolean showGenderChooser = false;

	private boolean showGenderChooserReadOnly = false;

	private boolean showNameAndPersonalID = false;
	private boolean nameAndPersonalIDDisabled = true;

	public CitizenAccountPreferences() {
	}

	@Override
	public void present(IWContext iwc) {
		if (!iwc.isLoggedOn()) {
			return;
		}
		this.iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		this.user = getUser(iwc);
		try {
			int action = parseAction(iwc);
			switch (action) {
				case ACTION_VIEW_FORM:
					viewForm(iwc);
					break;
				case ACTION_FORM_SUBMIT:
					updatePreferences(iwc);
					break;
			}
		}
		catch (Exception e) {
			super.add(new ExceptionWrapper(e, this));
		}
	}

	protected User getUser(IWContext iwc) {
		return iwc.getCurrentUser();
	}

	private int parseAction(final IWContext iwc) {
		int action = ACTION_VIEW_FORM;
		if (iwc.isParameterSet(PARAMETER_FORM_SUBMIT)) {
			action = ACTION_FORM_SUBMIT;
		}
		return action;
	}

	protected Layer getHeader(String text) {
		Layer header = new Layer(Layer.DIV);
		header.setStyleClass("header");

		Heading1 heading = new Heading1(text);
		header.add(heading);

		return header;
	}

	protected void viewForm(IWContext iwc) throws Exception {
		add(getPreferencesForm(iwc));
	}

	protected Form getPreferencesForm(IWContext iwc) throws Exception {
		Form form = new Form();
		form.setMultiPart();
		form.addParameter(PARAMETER_FORM_SUBMIT, Boolean.TRUE.toString());
		form.setID("citizenAccountPreferences");
		form.setStyleClass("citizenForm");

		form.add(getHeader(this.iwrb.getLocalizedString("citizen_preferences", "Citizen preferences")));

		Layer contents = new Layer(Layer.DIV);
		contents.setStyleClass("formContents");
		form.add(contents);

		Layer section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		contents.add(section);

		UserBusiness ub = (UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class);

		Image image = null;
		if (this.user.getSystemImageID() > 0) {
			try {
				image = new Image(this.user.getSystemImageID());
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}

		Email mail = ub.getUserMail(this.user);
		Phone homePhone = null;
		try {
			homePhone = ub.getUsersHomePhone(this.user);
		}
		catch (NoPhoneFoundException e) {}

		Phone mobilePhone = null;
		try {
			mobilePhone = ub.getUsersMobilePhone(this.user);
		}
		catch (NoPhoneFoundException e) {}

		Phone workPhone = null;
		try {
			workPhone = ub.getUsersWorkPhone(this.user);
		}
		catch (NoPhoneFoundException e) {}

		Address coAddress = getCOAddress(iwc);
		PostalCode postal = null;
		if (coAddress != null) {
			postal = coAddress.getPostalCode();
		}

		FileInput file = new FileInput();

		TextInput tiEmail = new TextInput(PARAMETER_EMAIL);
		if (mail != null && mail.getEmailAddress() != null) {
			tiEmail.setContent(mail.getEmailAddress());
		}

		TextInput tiPhoneHome = new TextInput(PARAMETER_PHONE_HOME);
		if (homePhone != null && homePhone.getNumber() != null) {
			tiPhoneHome.setContent(homePhone.getNumber());
		}

		TextInput tiPhoneMobile = new TextInput(PARAMETER_PHONE_MOBILE);
		if (mobilePhone != null && mobilePhone.getNumber() != null) {
			tiPhoneMobile.setContent(mobilePhone.getNumber());
		}

		TextInput tiPhoneWork = new TextInput(PARAMETER_PHONE_WORK);
		if (workPhone != null && workPhone.getNumber() != null) {
			tiPhoneWork.setContent(workPhone.getNumber());
		}

		CitizenAccountSession cas = getCitizenAccountSession(iwc);
		CheckBox useCOAddress = new CheckBox(PARAMETER_CO_ADDRESS_SELECT, "true");
		useCOAddress.setStyleClass("checkbox");
		useCOAddress.setChecked(cas.getIfUserUsesCOAddress());
		useCOAddress.keepStatusOnAction(true);

		TextInput tiCOStreetAddress = new TextInput(PARAMETER_CO_STREET_ADDRESS);
		if (coAddress != null && coAddress.getStreetAddress() != null) {
			tiCOStreetAddress.setContent(coAddress.getStreetAddress());
		}
		tiCOStreetAddress.setDisabled(!cas.getIfUserUsesCOAddress());
		useCOAddress.setToDisableWhenUnchecked(tiCOStreetAddress);
		useCOAddress.setToEnableWhenChecked(tiCOStreetAddress);

		TextInput tiCOPostalCode = new TextInput(PARAMETER_CO_POSTAL_CODE);
		if (postal != null && postal.getPostalCode() != null) {
			tiCOPostalCode.setValue(postal.getPostalCode());
		}
		tiCOPostalCode.setDisabled(!cas.getIfUserUsesCOAddress());
		useCOAddress.setToDisableWhenUnchecked(tiCOPostalCode);
		useCOAddress.setToEnableWhenChecked(tiCOPostalCode);

		TextInput tiCOCity = new TextInput(PARAMETER_CO_CITY);
		if (coAddress != null && coAddress.getCity() != null) {
			tiCOCity.setValue(coAddress.getCity());
		}
		tiCOCity.setDisabled(!cas.getIfUserUsesCOAddress());
		useCOAddress.setToDisableWhenUnchecked(tiCOCity);
		useCOAddress.setToEnableWhenChecked(tiCOCity);

		CountryDropdownMenu tiCOCountry = new CountryDropdownMenu(PARAMETER_CO_COUNTRY);
		if (postal != null && postal.getCountryID() > -1) {
			tiCOCountry.setSelectedCountry(postal.getCountry());
		}
		tiCOCountry.setDisabled(!cas.getIfUserUsesCOAddress());
		useCOAddress.setToDisableWhenUnchecked(tiCOCountry);
		useCOAddress.setToEnableWhenChecked(tiCOCountry);

		MessageSession messageSession = getMessageSession(iwc);
		CheckBox messagesViaEmail = new CheckBox(PARAMETER_MESSAGES_VIA_EMAIL, "true");
		messagesViaEmail.setStyleClass("checkbox");
		messagesViaEmail.keepStatusOnAction(true);
		messagesViaEmail.setChecked(messageSession.getIfUserPreferesMessageByEmail());

		CheckBox removeImage = new CheckBox(PARAMETER_REMOVE_IMAGE, "true");
		removeImage.setStyleClass("checkbox");
		removeImage.keepStatusOnAction(true);

		if (isSetToShowNameAndPersonalID()) {
			getUserInputs(iwc, form, section);
		}

		Layer layer = new Layer(Layer.DIV);
		layer.setID("citizenImage");
		section.add(layer);

		Layer helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("citizen_preferences_image_help", "To remove the displayed image check the checkbox and save.")));
		layer.add(helpLayer);

		if (image != null) {
			createFormItem(null, null, this.iwrb.getLocalizedString("image", "Image"), null, image, layer);
			createFormItem("indentedCheckbox", "removeImage", this.iwrb.getLocalizedString("remove_image", "Remove image"), removeImage, layer);
		}

		layer.add(new CSSSpacer());

		createFormItem("imageUpload", this.iwrb.getLocalizedString("image_upload", "Image upload"), file, layer);

		section.add(new CSSSpacer());

		layer = new Layer(Layer.DIV);
		layer.setID("citizenEmail");
		section.add(layer);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("citizen_preferences_email_help", "Here you can change your current email address or choose not to be notified by email. You will still receive messages under your account even if you do.")));
		layer.add(helpLayer);

		createFormItem(this.iwrb.getLocalizedString(KEY_EMAIL, DEFAULT_EMAIL), tiEmail, layer);

		createFormItem("indentedCheckbox", "messagesViaEmail", this.iwrb.getLocalizedString(KEY_MESSAGES_VIA_EMAIL, DEFAULT_MESSAGES_VIA_EMAIL), messagesViaEmail,
				layer);

		section.add(new CSSSpacer());

		layer = new Layer(Layer.DIV);
		layer.setID("citizenPhones");
		section.add(layer);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("citizen_preferences_phones_help", "Here you can modify your phone information or delete the numbers by leaving the fields empty.")));
		layer.add(helpLayer);

		createFormItem("homePhone", this.iwrb.getLocalizedString(KEY_PHONE_HOME, DEFAULT_PHONE_HOME), tiPhoneHome, layer);

		createFormItem("workPhone", this.iwrb.getLocalizedString(KEY_PHONE_WORK, DEFAULT_PHONE_WORK), tiPhoneWork, layer);

		createFormItem("mobilePhone", this.iwrb.getLocalizedString(KEY_PHONE_MOBILE, DEFAULT_PHONE_MOBILE), tiPhoneMobile, layer);

		section.add(new CSSSpacer());

		layer = new Layer(Layer.DIV);
		layer.setID("citizenResidence");
		section.add(layer);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("citizen_preferences_residence_help", "If you would like to receive letters via your C/O address, rather than your registered one, you can check the checkbox below and fill in your C/O address.")));
		layer.add(helpLayer);

		createFormItem("indentedCheckbox", null, this.iwrb.getLocalizedString(KEY_CO_ADDRESS_SELECT, DEFAULT_CO_ADDRESS_SELECT), useCOAddress, layer);

		createFormItem(this.iwrb.getLocalizedString(KEY_CO_STREET_ADDRESS, DEFAULT_CO_STREET_ADDRESS), tiCOStreetAddress, layer);

		createFormItem(this.iwrb.getLocalizedString(KEY_CO_POSTAL_CODE, DEFAULT_CO_POSTAL_CODE), tiCOPostalCode, layer);

		createFormItem(this.iwrb.getLocalizedString(KEY_CO_CITY, DEFAULT_CO_CITY), tiCOCity, layer);

		createFormItem(this.iwrb.getLocalizedString(KEY_CO_COUNTRY, DEFAULT_CO_COUNTRY), tiCOCountry, layer);

		layer = new Layer(Layer.DIV);
		section.add(layer);

		if(isSetToShowGenderChooser() || isSetToShowGenderChooserReadOnly()){
			GenderHome genderHome = (GenderHome) IDOLookup.getHome(Gender.class);
			String maleId = genderHome.getMaleGender().getPrimaryKey().toString();
			String femaleId = genderHome.getFemaleGender().getPrimaryKey().toString();

			String userGenderId = null;
			Gender userGender = user.getGender();
			userGenderId = userGender == null ? CoreConstants.EMPTY : userGender.getPrimaryKey().toString();

			RadioGroup gender = new RadioGroup(PARAMETER_GENDER);
			gender.addRadioButton(maleId, new Text(this.iwrb.getLocalizedString("male", "Male")), userGenderId.equals(maleId));
			gender.addRadioButton(femaleId, new Text(this.iwrb.getLocalizedString("female", "Female")), userGenderId.equals(femaleId));

			if(isSetToShowGenderChooserReadOnly()){
				gender.setReadOnly(true);
			}

			createFormItem("citizenGender", this.iwrb.getLocalizedString("gender", "Gender"), gender, layer);
		}


		if(isSetToShowPreferredLocaleChooser()){
			DropdownMenu localesDrop = LocalePresentationUtil.getAvailableLocalesDropdown(iwc.getIWMainApplication(), PARAMETER_PREFERRED_LOCALE);

			if (localesDrop.getChildCount() > 1) {
				section.add(new CSSSpacer());

				if (user.getPreferredLocale() != null) {
					localesDrop.setSelectedElement(user.getPreferredLocale());
				}
				else {
					localesDrop.setSelectedElement(iwc.getCurrentLocale().toString());
				}

				createFormItem("preferredLang", this.iwrb.getLocalizedString(PREFERRED_LANGUAGE, "Preferred language"), localesDrop, layer);
			}
		}

		if(isSetToShowPreferredRoleChooser()){
			DropdownMenu rolesDrop = new DropdownMenu(PARAMETER_PREFERRED_ROLE);
			List<ICRole> rolesForUser = ub.getAvailableRolesForUserAsPreferredRoles(user);
			IWResourceBundle coreIWRB = iwc.getIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
			if (!ListUtil.isEmpty(rolesForUser)) {
				for (ICRole role: rolesForUser) {
					String localisedPermissionName = coreIWRB.getLocalizedString(role.getRoleNameLocalizableKey(), role.getRoleKey());
					rolesDrop.addMenuElement(role.getId(), localisedPermissionName);
				}
			}

			if (rolesDrop.getChildCount() > 1) {
				section.add(new CSSSpacer());

				if (user.getPreferredRole() != null) {
					rolesDrop.setSelectedElement(user.getPreferredRole().getId());
				}

				createFormItem("preferredRole", this.iwrb.getLocalizedString(PREFERRED_ROLE, "Preferred user role"), rolesDrop, layer);
			}
		}

		section.add(new CSSSpacer());

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		contents.add(buttonLayer);

		Layer span = new Layer(Layer.SPAN);
		span.add(new Text(this.iwrb.getLocalizedString(KEY_UPDATE, DEFAULT_UPDATE)));
		Link send = new Link(span);
		send.setToFormSubmit(form);
		buttonLayer.add(send);

		return form;
	}

	protected void getUserInputs(IWContext iwc, Form form, Layer section) {
		TextInput name = new TextInput(PARAMETER_NAME, user.getName());
		createFormItem(this.iwrb.getLocalizedString("name", "Name"), name, section);

		if (isNameAndPersonalIDDisabled()) {
			name.setDisabled(true);
			section.add(new HiddenInput(PARAMETER_NAME, user.getName()));
		}

		TextInput ssn = new TextInput(PARAMETER_SSN, user.getPersonalID());
		createFormItem(this.iwrb.getLocalizedString("social_security_number", "Social security number"), ssn, section);

		if (isNameAndPersonalIDDisabled()) {
			ssn.setDisabled(true);
			section.add(new HiddenInput(PARAMETER_SSN, user.getPersonalID()));
		}
	}

	public boolean isSetToShowGenderChooserReadOnly() {
		return showGenderChooserReadOnly;
	}

	protected void createFormItem(String label, InterfaceObject uiObject, UIComponent container) {
		createFormItem(null, null, label, uiObject, null, container);
	}

	private void createFormItem(String formItemId, String label, InterfaceObject uiObject, UIComponent container) {
		createFormItem(null, formItemId, label, uiObject, null, container);
	}

	private void createFormItem(String formItemStyle, String formItemId, String label, InterfaceObject uiObject, UIComponent container) {
		createFormItem(formItemStyle, formItemId, label, uiObject, null, container);
	}

	private void createFormItem(String formItemStyle, String formItemId, String label, InterfaceObject uiObject, UIComponent child, UIComponent container) {
		Layer formItem = getFormItem(formItemStyle, formItemId, label, uiObject, child);
		container.getChildren().add(formItem);
	}

	protected Layer getFormItem(String formItemStyle, String formItemId, String label, InterfaceObject uiObject, UIComponent child) {
		Layer formItem = new Layer();
		formItem.setStyleClass("formItem");
		if (!StringUtil.isEmpty(formItemStyle)) {
			formItem.setStyleClass(formItemStyle);
		}
		if (!StringUtil.isEmpty(formItemId)) {
			formItem.setId(formItemId);
		}

		Label uiLabel = null;
		if (uiObject == null) {
			uiLabel = new Label();
			uiLabel.add(new Text(label));
		} else {
			uiLabel = new Label(label, uiObject);
		}

		if (uiObject != null) {
			if (uiObject instanceof CheckBox) {
				formItem.add(uiObject);
				formItem.add(uiLabel);
			} else {
				formItem.add(uiLabel);
				formItem.add(uiObject);
			}
		} else {
			formItem.add(uiLabel);
		}

		if (child != null) {
			formItem.add(child);
		}

		return formItem;
	}

	protected boolean updatePreferences(IWContext iwc) throws Exception {
		Collection<String> errors = new ArrayList<String>();

		int fileID = -1;
		UploadFile uploadFile = iwc.getUploadedFile();
		if (uploadFile != null && uploadFile.getName() != null && uploadFile.getName().length() > 0) {
			try {
				FileInputStream input = new FileInputStream(uploadFile.getRealPath());

				ICFile file = ((com.idega.core.file.data.ICFileHome) com.idega.data.IDOLookup.getHome(ICFile.class)).create();
				file.setName(uploadFile.getName());
				file.setMimeType(uploadFile.getMimeType());
				file.setFileValue(input);
				file.setFileSize((int) uploadFile.getSize());
				file.store();

				fileID = ((Integer) file.getPrimaryKey()).intValue();
				uploadFile.setId(fileID);
				try {
					FileUtil.delete(uploadFile);
				}
				catch (Exception ex) {
					System.err.println("MediaBusiness: deleting the temporary file at " + uploadFile.getRealPath() + " failed.");
				}
			}
			catch (RemoteException e) {
				e.printStackTrace(System.err);
				uploadFile.setId(-1);
			}
		}

		UserBusiness ub = IBOLookup.getServiceInstance(iwc, UserBusiness.class);

		String name = iwc.getParameter(PARAMETER_NAME);
		String ssn = iwc.getParameter(PARAMETER_SSN);
		String sEmail = iwc.isParameterSet(PARAMETER_EMAIL) ? iwc.getParameter(PARAMETER_EMAIL) : null;
		String phoneHome = iwc.getParameter(PARAMETER_PHONE_HOME);
		String phoneMobile = iwc.getParameter(PARAMETER_PHONE_MOBILE);
		String phoneWork = iwc.getParameter(PARAMETER_PHONE_WORK);
		String coStreetAddress = iwc.getParameter(PARAMETER_CO_STREET_ADDRESS);
		String coPostalCode = iwc.getParameter(PARAMETER_CO_POSTAL_CODE);
		String coCity = iwc.getParameter(PARAMETER_CO_CITY);
		String coCountry = iwc.getParameter(PARAMETER_CO_COUNTRY);
		String gender = iwc.getParameter(PARAMETER_GENDER);
		String preferredLocale = iwc.getParameter(PARAMETER_PREFERRED_LOCALE);
		String preferredRoleID = iwc.getParameter(PARAMETER_PREFERRED_ROLE);
		boolean useCOAddress = iwc.isParameterSet(PARAMETER_CO_ADDRESS_SELECT);
		boolean messagesViaEmail = iwc.isParameterSet(PARAMETER_MESSAGES_VIA_EMAIL);
		boolean removeImage = iwc.isParameterSet(PARAMETER_REMOVE_IMAGE);
		boolean removeSSN = StringUtil.isEmpty(ssn);
		boolean updateGender = !StringUtil.isEmpty(gender);

		if (isSetToShowNameAndPersonalID() && StringUtil.isEmpty(name)) {
			errors.add(this.iwrb.getLocalizedString("invalid_name", "Name is invalid"));
		}

		if (!removeSSN) {
			if (!ub.validatePersonalId(ssn, iwc.getCurrentLocale())) {
				errors.add(new StringBuilder(this.iwrb.getLocalizedString("invalid_ssn", "SSN is invalid")).append(CoreConstants.COLON)
						.append(CoreConstants.SPACE).append(ssn).toString());
			}
		}

		Integer genderId = null;
		if (updateGender) {
			try {
				genderId = Integer.valueOf(gender);
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
			if (genderId == null || genderId.intValue() < 0) {
				errors.add(this.iwrb.getLocalizedString("invalid_gender", "Invalid gender"));
			}
		}

		boolean updateEmail = false;
		if (sEmail != null) {
			updateEmail = EmailValidator.getInstance().validateEmail(sEmail);
			if (!updateEmail) {
				errors.add(this.iwrb.getLocalizedString(KEY_EMAIL_INVALID, DEFAULT_EMAIL_INVALID));
			}
		}
		/*
		 * IF user checks that he wants all letters sent by email but doesn't enter a valid email address he should get a warning
		 */
		if (messagesViaEmail && !updateEmail) {
			errors.add(this.iwrb.getLocalizedString(KEY_NO_EMAIL_FOR_LETTERS, DEFAULT_NO_EMAIL_FOR_LETTERS));
		}

		// Validate c/o-address
		if (useCOAddress) {
			if (coStreetAddress.equals("")) {
				errors.add(this.iwrb.getLocalizedString(KEY_CO_STREET_ADDRESS_MISSING, DEFAULT_CO_STREET_ADDRESS_MISSING));
			}
			if (coPostalCode.equals("")) {
				errors.add(this.iwrb.getLocalizedString(KEY_CO_POSTAL_CODE_MISSING, DEFAULT_CO_POSTAL_CODE_MISSING));
			}
			if (coCity.equals("")) {
				errors.add(this.iwrb.getLocalizedString(KEY_CO_CITY_MISSING, DEFAULT_CO_CITY_MISSING));
			}
		}

		if (ListUtil.isEmpty(errors)) {
			//	No errors found

			if (isSetToShowNameAndPersonalID() && !isNameAndPersonalIDDisabled()) {
				//	Name
				user.setFullName(name);

				//	SSN
				user.setPersonalID(removeSSN ? null : ssn);
			}

			//	Gender
			if (updateGender){
				user.setGender(genderId);
			}

			user.store();

			if (updateEmail) {
				ub.storeUserEmail(this.user, sEmail, true);
			}
			ub.updateUserHomePhone(this.user, phoneHome);
			ub.updateUserWorkPhone(this.user, phoneWork);
			ub.updateUserMobilePhone(this.user, phoneMobile);

			if (preferredLocale != null) {
				ub.setUsersPreferredLocale(user, preferredLocale, true);
			}
			if (preferredRoleID != null) {
				IWMainApplication app = iwc.getIWMainApplication();
				ICRole role = app.getAccessController().getRoleByRoleKeyOld(preferredRoleID);
				ub.setUsersPreferredRole(user, role, true);
			}

			if (useCOAddress) {
				Address coAddress = getCOAddress(iwc);
				//coAddress.setStreetName(coStreetAddress);

				AddressBusiness addressBusiness = (AddressBusiness) IBOLookup.getServiceInstance(iwc, AddressBusiness.class);
				Country country = addressBusiness.getCountryHome().findByPrimaryKey(new Integer(coCountry));
				PostalCode pc = addressBusiness.getPostalCodeAndCreateIfDoesNotExist(coPostalCode, coCity, country);
				String streetName = addressBusiness.getStreetNameFromAddressString(coStreetAddress);
				String streetNumber = addressBusiness.getStreetNumberFromAddressString(coStreetAddress);
				coAddress.setStreetName(streetName);
				coAddress.setStreetNumber(streetNumber);

				coAddress.setPostalCode(pc);
				coAddress.setCity(coCity);
				coAddress.store();
			}
			MessageSession messageSession = getMessageSession(iwc);
			messageSession.setIfUserPreferesMessageByEmail(messagesViaEmail);
			CitizenAccountSession cas = getCitizenAccountSession(iwc);
			cas.setIfUserUsesCOAddress(useCOAddress);

			if (removeImage) {
				this.user.setSystemImageID(null);
				this.user.store();
			}
			if (fileID != -1) {
				this.user.setSystemImageID(fileID);
				this.user.store();
			}

			//send to external system if needed
			if (iwc.getApplicationSettings().getBoolean("SEND_USER_INFO_TO_EXTERNAL", false)) {
				WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(iwc.getServletContext());
				Object bean = springContext.getBean(iwc.getApplicationSettings().getProperty("SEND_USER_INFO_SERVICE_NAME", "linnaWSBusiness"));
				if (bean instanceof UserInfoToExternalSystemService) {
					UserInfoToExternalSystemService service = (UserInfoToExternalSystemService) bean;
					
					String identifycationString = iwc.getApplicationSettings().getProperty("SEND_USER_INFO_IDENTIFICATION_STRING", "RR");
					
					service.updateUserInfo(iwc.getCurrentUser().getPersonalID(), sEmail, phoneHome, phoneWork, phoneMobile, identifycationString);
				} else {
					getLogger().warning("Unable to update user info. Expected instance of " + UserInfoToExternalSystemService.class.getName() + ", got: " +
							(bean == null ? "null" : bean.getClass().getName()));
				}
			}
			
			Layer header = new Layer(Layer.DIV);
			header.setStyleClass("header");
			add(header);

			Heading1 heading = new Heading1(this.iwrb.getLocalizedString("citizen_preferences", "Citizen preferences"));
			header.add(heading);

			Layer layer = new Layer(Layer.DIV);
			layer.setStyleClass("receipt");

			Layer image = new Layer(Layer.DIV);
			image.setStyleClass("receiptImage");
			layer.add(image);

			heading = new Heading1(this.iwrb.getLocalizedString(KEY_PREFERENCES_SAVED, DEFAULT_PREFERENCES_SAVED));
			layer.add(heading);

			Paragraph paragraph = new Paragraph();
			paragraph.add(new Text(this.iwrb.getLocalizedString(KEY_PREFERENCES_SAVED + "_text", DEFAULT_PREFERENCES_SAVED + " info")));
			layer.add(paragraph);

			try {
				ICPage page = ub.getHomePageForUser(this.user);
				paragraph.add(new Break(2));

				Layer span = new Layer(Layer.SPAN);
				span.add(new Text(this.iwrb.getLocalizedString("my_page", "My page")));
				Link link = new Link(span);
				link.setStyleClass("homeLink");
				link.setPage(page);
				paragraph.add(link);
			}
			catch (FinderException fe) {
				// No homepage found...
			}

			add(layer);

			return true;
		}
		else {
			showErrors(iwc, errors);
			viewForm(iwc);
		}

		return false;
	}

	private Address getCOAddress(IWContext iwc) {
		Address coAddress = null;
		try {
			UserBusiness ub = (UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class);
			AddressHome ah = ub.getAddressHome();
			AddressType coType = ah.getAddressType2();

			Address address = ub.getUserAddressByAddressType(new Integer(user.getPrimaryKey().toString()).intValue(), coType);
			if (address != null) {
				return address;
			}
			coAddress = ah.create();
			coAddress.setAddressType(coType);
			coAddress.store();
			this.user.addAddress(coAddress);
		}
		catch (Exception e) {
			super.add(new ExceptionWrapper(e, this));
		}
		return coAddress;
	}

	private MessageSession getMessageSession(IWContext iwc) throws RemoteException {
		return (MessageSession) com.idega.business.IBOLookup.getSessionInstance(iwc, MessageSession.class);
	}

	private CitizenAccountSession getCitizenAccountSession(IWContext iwc) throws RemoteException {
		return (CitizenAccountSession) com.idega.business.IBOLookup.getSessionInstance(iwc, CitizenAccountSession.class);
	}

	public void setToRemoveEmailWhenEmpty(boolean flag) {
	}

	/**
	 * @return Returns the showPreferredLocaleChooser.
	 */
	public boolean isSetToShowPreferredLocaleChooser() {
		return showPreferredLocaleChooser;
	}

	public boolean isSetToShowPreferredRoleChooser() {
		return showPreferredRoleChooser;
	}

	public boolean isSetToShowGenderChooser() {
		return showGenderChooser;
	}

	private boolean isSetToShowNameAndPersonalID() {
		return showNameAndPersonalID;
	}

	/**
	 * @param showPreferredLocaleChooser
	 *          The showPreferredLocaleChooser to set.
	 */
	public void setToShowPreferredLocaleChooser(boolean showPreferredLocaleChooser) {
		this.showPreferredLocaleChooser = showPreferredLocaleChooser;
	}

	/**
	 * @param showPreferredRoleChooser
	 *          The showPreferredRoleChooser to set.
	 */
	public void setToShowPreferredRoleChooser(boolean showPreferredRoleChooser) {
		this.showPreferredRoleChooser = showPreferredRoleChooser;
	}

	/**
	 * @param showGenderChooser
	 *          The showGenderChooser to set.
	 */
	public void setToShowGenderChooser(boolean showGenderChooser) {
		this.showGenderChooser = showGenderChooser;
	}

	public void setToShowNameAndPersonalID(boolean showNameAndPersonalID) {
		this.showNameAndPersonalID = showNameAndPersonalID;
	}

	public void setToShowGenderChooserReadOnly(boolean showGenderChooserReadOnly) {
		this.showGenderChooserReadOnly = showGenderChooserReadOnly;
	}

	protected User getUser() {
		return this.user;
	}

	public boolean isNameAndPersonalIDDisabled() {
		return nameAndPersonalIDDisabled;
	}

	public void setNameAndPersonalIDDisabled(boolean nameAndPersonalIDDisabled) {
		this.nameAndPersonalIDDisabled = nameAndPersonalIDDisabled;
	}
}