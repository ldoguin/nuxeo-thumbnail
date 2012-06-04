/*
 * Copyright (c) 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * ldoguin
 * 
 */
package org.nuxeo.thumb;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

/**
 * @author ldoguin
 */
public class Thumbnail {

	protected final DocumentModel doc;

	public Thumbnail(DocumentModel doc) {
		this.doc = doc;
	}

	public Blob getThumbnail() throws PropertyException, ClientException {
		return (Blob) doc
				.getPropertyValue(ThumbnailConstants.THUMBNAIL_PROPERTY_NAME);
	}

	public String getDigest() throws PropertyException, ClientException {
		return (String) doc
				.getPropertyValue(ThumbnailConstants.THUMBNAIL_DIGEST_PROPERTY_NAME);

	}
}
