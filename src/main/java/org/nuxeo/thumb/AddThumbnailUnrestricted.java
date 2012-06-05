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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.platform.filemanager.api.FileManager;
import org.nuxeo.runtime.api.Framework;

/**
 * @author ldoguin
 */
public class AddThumbnailUnrestricted extends UnrestrictedSessionRunner {

	private static final Log log = LogFactory
			.getLog(AddThumbnailUnrestricted.class);

	protected ConversionService cs;

	protected DocumentModel doc;

	protected BlobHolder blobHolder;

	protected Thumbnail thumbnail = null;

	public AddThumbnailUnrestricted(CoreSession coreSession, DocumentModel doc,
			BlobHolder blobHolder) {
		super(coreSession);
		this.doc = doc;
		this.blobHolder = blobHolder;
	}

	@Override
	public void run() throws ClientException {
		try {
			Blob blob = blobHolder.getBlob();
			if (blob != null) {
				// computing the digest will be unnecessary in 5.6,
				// we use the one from VCS
				FileManager fm = Framework.getService(FileManager.class);
				String digest = fm.computeDigest(blob);
				if (doc.hasFacet(ThumbnailConstants.THUMBNAIL_FACET)) {
					Thumbnail thumb = doc.getAdapter(Thumbnail.class);
					// document already has Thumbnail Facet
					String previousDigest = thumb.getDigest();
					if (previousDigest.equals(digest)) {
						// blob has not changed, no need for thumbnail update
						return;
					}
				}
				Map<String, Serializable> params = new HashMap<String, Serializable>();
				params.put(ThumbnailConstants.THUMBNAIL_SIZE_PARAMETER_NAME, ThumbnailConstants.THUMBNAIL_DEFAULT_SIZE);
				cs = Framework.getService(ConversionService.class);
				BlobHolder thumbnailBlob = cs.convert(
						ThumbnailConstants.THUMBNAIL_CONVERTER_NAME,
						blobHolder, params);
				if (thumbnailBlob != null) {
					// we can compute a thumbnail, add it to the document.
					if (!doc.hasFacet(ThumbnailConstants.THUMBNAIL_FACET)) {
						doc.addFacet(ThumbnailConstants.THUMBNAIL_FACET);
					}
					doc.setPropertyValue(
							ThumbnailConstants.THUMBNAIL_PROPERTY_NAME,
							(Serializable) thumbnailBlob.getBlob());
					doc.setPropertyValue(
							ThumbnailConstants.THUMBNAIL_DIGEST_PROPERTY_NAME,
							digest);
					doc = session.saveDocument(doc);
					thumbnail = new Thumbnail(doc);
				}
			}
		} catch (Exception e) {
			log.warn("Error while adding thumbnail", e);
		}
	}

	public Thumbnail getAdapter() {
		return thumbnail;
	}

}
