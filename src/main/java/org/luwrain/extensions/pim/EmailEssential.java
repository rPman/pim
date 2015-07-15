package org.luwrain.extensions.pim;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public interface EmailEssential
{
	// make MimeMessage from class fields
	public void PrepareInternalStore(EmailMessage msg) throws Exception;
	// used to fill fields via .eml file stream
	public EmailMessage LoadEmailFromFile(FileInputStream fs) throws Exception;
	// used to save fields to .eml field stream
	public void SaveEmailToFile(EmailMessage msg,FileOutputStream fs) throws Exception;

}
