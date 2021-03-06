/*
 * $Id$
 * Created on 6.4.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.citizen.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.block.process.data.CaseStatus;
import com.idega.data.IDOException;
import com.idega.data.IDOHome;


/**
 * <p>
 * TODO anna Describe Type CitizenAccountHome
 * </p>
 *  Last modified: $Date$ by $Author$
 * 
 * @author <a href="mailto:anna@idega.com">anna</a>
 * @version $Revision$
 */
public interface CitizenAccountHome extends IDOHome {

	public CitizenAccount create() throws javax.ejb.CreateException;

	public CitizenAccount findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see se.idega.idegaweb.commune.account.citizen.data.CitizenAccountBMPBean#ejbFindAllCasesByStatus
	 */
	public Collection findAllCasesByStatus(CaseStatus caseStatus) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.account.citizen.data.CitizenAccountBMPBean#ejbFindAllCasesByStatus
	 */
	public Collection findAllCasesByStatus(String caseStatus) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.account.citizen.data.CitizenAccountBMPBean#ejbHomeGetTotalCount
	 */
	public int getTotalCount() throws IDOException;

	/**
	 * @see se.idega.idegaweb.commune.account.citizen.data.CitizenAccountBMPBean#ejbHomeGetCount
	 */
	public int getCount(String personalID, String status) throws IDOException;
}
