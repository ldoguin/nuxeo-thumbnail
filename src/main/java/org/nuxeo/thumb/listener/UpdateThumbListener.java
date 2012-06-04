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
package org.nuxeo.thumb.listener;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.thumb.AddThumbnailUnrestricted;
import org.nuxeo.thumb.ThumbnailConstants;

/**
 * @author ldoguin
 */
public class UpdateThumbListener implements EventListener {

	public void handleEvent(Event event) throws ClientException {
		EventContext ec = event.getContext();
		if (ec instanceof DocumentEventContext) {
			DocumentEventContext context = (DocumentEventContext) ec;
			DocumentModel doc = context.getSourceDocument();
			if (doc.isDirty() || DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName())) {
				BlobHolder blobHolder = doc.getAdapter(BlobHolder.class);
				if (blobHolder != null) {
					Blob blob = blobHolder.getBlob();
					if (blob != null) {
						try {
							AddThumbnailUnrestricted runner = new AddThumbnailUnrestricted(
									context.getCoreSession(), doc, blobHolder);
							runner.runUnrestricted();
							return;
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
				// No Blob anymore, remove the facet
				if (doc.hasFacet(ThumbnailConstants.THUMBNAIL_FACET)) {
					doc.removeFacet(ThumbnailConstants.THUMBNAIL_FACET);
					context.getCoreSession().saveDocument(doc);
				}
			}
		}
	}

}
