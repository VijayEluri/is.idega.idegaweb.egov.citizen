package is.idega.idegaweb.egov.citizen.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import se.idega.idegaweb.commune.account.citizen.business.CitizenAccountBusiness;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.builder.data.ICPage;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.text.SocialSecurityNumber;

/**
 * *
 * 
 * Title: idegaWeb Description: This class handles the case, when a user has
 * forgotten his password. The presentation provides a single input field for
 * the personal id. After submitting the input is checked. If the inputfield is
 * empty a warning dialog pops up. If the input represents an impossible social
 * security number (SSN) an error message is returned (with the inputfield
 * again). If the input is a possible valid ssn the value is checked if this ssn
 * can be found in the table of already known citizen in the database. If the
 * ssn is unknown a link to the citizen application form is returned. If the ssn
 * is known it is checked if the citizen has already activated his account. If
 * the citizen has not an activated account yet again a link to to the citizen
 * application form is returned. There are two different cases if the citizen
 * has already an activated account: If the user has never logged in you can not
 * trust his registered email address. Therefore a new password is generated and
 * send by regular post to the person. If the the user has logged at some time a
 * new password is generated and send by email.
 * 
 * Copyright: Copyright (c) 2002 Company: idega software
 * 
 * @author <a href="mailto:thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 */
public class ForgottenPassword extends CitizenBlock {

	private final static String SSN_KEY = "personal_id";
	private final static String SSN_DEFAULT = "Personal ID";
	private final static String COMMUNE_DEFAULT = "Commune";
	private final static String COMMUNE_KEY = "commmune";
	private final static String PASSWORD_CREATED_KEY = "password_was_created";
	private final static String PASSWORD_CREATED_DEFAULT = "A new password was generated.";
	private final static String FORM_SUBMIT_KEY = "form_submit_key";
	private final static String FORM_SUBMIT_DEFAULT = "Forgot my password";
	private final static String ACTION_VIEW_FORM = "action_view_form";
	private final static String ACTION_FORM_SUBMIT = "action_form_submit";
	private static final String hasAppliedForPsw = "has_applied_before";
	
	private IWResourceBundle iwrb;
	private ICPage iPage;
	private int iRedirectDelay = 15;

	private boolean iForwardToURL = false;
	private Map iCommuneMap;

	public void present(IWContext iwc) {
		this.iwrb = getResourceBundle(iwc);

		String action = parseAction(iwc);
		if (ACTION_VIEW_FORM.equals(action)) {
			viewForm(iwc);
		}
		else if (ACTION_FORM_SUBMIT.equals(action)) {
			submitForm(iwc);
		}
	}

	/**
	 * Handles the input. Checks if the input is a possible valid ssn and if the
	 * user is known or unknown.
	 * 
	 * @param iwc
	 */
	private void submitForm(IWContext iwc) {
		if (iwc.isParameterSet(COMMUNE_KEY) && this.iForwardToURL) {
			String URL = iwc.getParameter(COMMUNE_KEY);
			StringBuffer query = new StringBuffer();
			Enumeration enumeration = iwc.getParameterNames();
			if (enumeration != null) {
				query.append("?");
				
				while (enumeration.hasMoreElements()) {
					String element = (String) enumeration.nextElement();
					query.append(element).append("=").append(iwc.getParameter(element));
					if (enumeration.hasMoreElements()) {
						query.append("&");
					}
				}
			}
			iwc.sendRedirect(URL + query.toString());
			return;
		}

		String ssn = iwc.getParameter(SSN_KEY);

		boolean hasErrors = false;
		boolean invalidPersonalID = false;
		Collection errors = new ArrayList();
		
		if (ssn == null ||ssn.length() == 0) {
			errors.add(this.iwrb.getLocalizedString("must_provide_personal_id", "You have to enter a personal ID."));
			hasErrors = true;
			invalidPersonalID = true;
		}
		else if (!SocialSecurityNumber.isValidIcelandicSocialSecurityNumber(ssn)) {
			errors.add(this.iwrb.getLocalizedString("not_a_valid_personal_id", "The personal ID you've entered is not valid."));
			hasErrors = true;
			invalidPersonalID = true;
		}

		if (iwc.getSessionAttribute(hasAppliedForPsw) != null) {
			errors.add(this.iwrb.getLocalizedString("already_applied_for_password", "You have already requested a new password."));
			hasErrors = true;
		}

		User user = null;
		if (!invalidPersonalID) {
			try {
				UserBusiness business = (UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class);
				user = business.getUser(ssn);
			}
			catch (RemoteException re) {
				throw new IBORuntimeException(re);
			}
			catch (FinderException ex) {
				errors.add(this.iwrb.getLocalizedString("no_user_found_with_personal_id", "No user was found with the personal ID you entered."));
				hasErrors = true;
			}
		}
		
		if (user != null) {
			LoginTable loginTable = LoginDBHandler.getUserLogin(user);
			if (loginTable == null) {
				errors.add(this.iwrb.getLocalizedString("no_login_found_for_user", "No login was found for the user with the personal ID you entered."));
				hasErrors = true;
			}
			else {
				String newPassword = createNewPassword();
				CitizenAccountBusiness business = getBusiness(iwc);
				try {
					business.changePasswordAndSendLetterOrEmail(iwc, loginTable, user, newPassword, false);
				}
				catch (RemoteException re) {
					throw new IBORuntimeException(re);
				}
				catch (CreateException ce) {
					ce.printStackTrace();
					errors.add(this.iwrb.getLocalizedString("password_creation_failed", "Password creation failed."));
					hasErrors = true;
				}
			}
		}
		
		if (!hasErrors) {
			iwc.setSessionAttribute(hasAppliedForPsw, Boolean.TRUE.toString());
			
			Layer header = new Layer(Layer.DIV);
			header.setStyleClass("header");
			add(header);
			
			Heading1 heading = new Heading1(this.iwrb.getLocalizedString("forgotten_password", "Forgotten password"));
			header.add(heading);
			
			Layer layer = new Layer(Layer.DIV);
			layer.setStyleClass("receipt");
			
			Layer image = new Layer(Layer.DIV);
			image.setStyleClass("receiptImage");
			layer.add(image);
			
			heading = new Heading1(this.iwrb.getLocalizedString(PASSWORD_CREATED_KEY, PASSWORD_CREATED_DEFAULT));
			layer.add(heading);
			
			layer.add(new Text(this.iwrb.getLocalizedString(PASSWORD_CREATED_KEY + "_text", PASSWORD_CREATED_DEFAULT + " info")));
			
			add(layer);
			
			if (this.iPage != null) {
				iwc.forwardToIBPage(getParentPage(), this.iPage, this.iRedirectDelay, false);
			}
		}
		else {
			showErrors(iwc, errors);
			viewForm(iwc);
		}
	}

	/**
	 * Builds a presentation containing the form with input field and submit
	 * button.
	 * 
	 * @param iwc
	 */
	private void viewForm(final IWContext iwc) {
		Form form = new Form();
		form.addParameter(FORM_SUBMIT_KEY, Boolean.TRUE.toString());
		form.setID("forgotPasswordForm");
		form.setStyleClass("citizenForm");
		
		Layer header = new Layer(Layer.DIV);
		header.setStyleClass("header");
		form.add(header);
		
		Heading1 heading = new Heading1(this.iwrb.getLocalizedString("forgotten_password", "Forgotten password"));
		header.add(heading);
		
		Layer section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(this.iwrb.getLocalizedString("forgot_password_helper_text", "Please enter your personal ID and click 'Send'.  A new password will be created and sent to your e-mail address.")));
		section.add(paragraph);
		
		TextInput input = new TextInput(SSN_KEY);
		input.keepStatusOnAction(true);

		if (this.iCommuneMap != null) {
			DropdownMenu communes = new DropdownMenu(COMMUNE_KEY);
			Iterator iter = this.iCommuneMap.keySet().iterator();
			while (iter.hasNext()) {
				String commune = (String) iter.next();
				communes.addMenuElement((String) this.iCommuneMap.get(commune), commune);
			}
			communes.addMenuElementFirst("", this.iwrb.getLocalizedString("select_commune", "Select commune"));

			Layer formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			Label label = new Label(communes);
			label.add(new Text(this.iwrb.getLocalizedString(COMMUNE_KEY, COMMUNE_DEFAULT)));
			formItem.add(label);
			formItem.add(communes);
			section.add(formItem);
		}

		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label(this.iwrb.getLocalizedString(SSN_KEY, SSN_DEFAULT), input);
		formItem.add(label);
		formItem.add(input);
		section.add(formItem);
		
		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		section.add(clearLayer);

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);
		
		Layer span = new Layer(Layer.SPAN);
		span.add(new Text(this.iwrb.getLocalizedString(FORM_SUBMIT_KEY + "_button", FORM_SUBMIT_DEFAULT)));
		Link send = new Link(span);
		send.setStyleClass("sendLink");
		send.setToFormSubmit(form);
		buttonLayer.add(send);
		
		add(form);
	}

	/**
	 * Parses the parameter string.
	 * 
	 * @param iwc
	 * @return either string for action "view form" or string for action "form was
	 *         submitted".
	 */
	private String parseAction(final IWContext iwc) {
		String action = ACTION_VIEW_FORM;
		if (iwc.isParameterSet(FORM_SUBMIT_KEY)) {
			action = ACTION_FORM_SUBMIT;
		}
		return action;
	}

	/**
	 * Looks up service bean citizen account business
	 * 
	 * @param iwc
	 * @return a service bean CitizenAccountBusiness.
	 */
	private CitizenAccountBusiness getBusiness(IWContext iwc) {
		try {
			return (CitizenAccountBusiness) IBOLookup.getServiceInstance(iwc, CitizenAccountBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	/**
	 * Creates a new unencrypted password.
	 * 
	 * @return an unencrypted password.
	 */
	private String createNewPassword() {
		return LoginDBHandler.getGeneratedPasswordForUser();
	}
	
	public void setPage(ICPage page) {
		this.iPage = page;
	}
	
	public void setRedirectDelay(int redirectDelay) {
		this.iRedirectDelay = redirectDelay;
	}

	public void setCommunePage(String name, String URL) {
		if (this.iCommuneMap == null) {
			this.iCommuneMap = new HashMap();
		}
		this.iCommuneMap.put(name, URL);
		this.iForwardToURL = true;
	}
}