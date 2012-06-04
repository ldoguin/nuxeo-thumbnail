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

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

/**
 * @author ldoguin
 */
public class ThumbnailFactory implements DocumentAdapterFactory {

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> itf) {
		return new Thumbnail(doc);
	}

}
