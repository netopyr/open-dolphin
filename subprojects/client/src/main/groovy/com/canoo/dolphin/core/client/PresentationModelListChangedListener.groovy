package com.canoo.dolphin.core.client

/**
 * @author Dieter Holz
 */
interface PresentationModelListChangedListener {
	void added(ClientPresentationModel pm)
	void removed(ClientPresentationModel pm)
}
