//
// Triple Play - utilities for use in PlayN-based games
// Copyright (c) 2011-2014, Three Rings Design, Inc. - All rights reserved.
// http://github.com/threerings/tripleplay/blob/master/LICENSE

package tripleplay.ui;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.ImmediateLayer;
import playn.core.Layer;
import playn.core.Surface;
import static playn.core.PlayN.graphics;

import pythagoras.f.Dimension;
import pythagoras.f.IDimension;

import tripleplay.ui.bgs.BeveledBackground;
import tripleplay.ui.bgs.BlankBackground;
import tripleplay.ui.bgs.BorderedBackground;
import tripleplay.ui.bgs.CenteredImageBackground;
import tripleplay.ui.bgs.CompositeBackground;
import tripleplay.ui.bgs.CroppedImageBackground;
import tripleplay.ui.bgs.ImageBackground;
import tripleplay.ui.bgs.RoundRectBackground;
import tripleplay.ui.bgs.Scale9Background;
import tripleplay.ui.bgs.SolidBackground;
import tripleplay.ui.bgs.TiledImageBackground;
import tripleplay.ui.util.Insets;
import tripleplay.util.Destroyable;

/**
 * A background is responsible for rendering a border and a fill. It is used in conjunction with
 * groups and buttons and any other elements that need a background.
 */
public abstract class Background
{
    /** An instantiation of a particular background template. Backgrounds are configured as a style
     * property; elements instantiate them at specific dimensions when they are actually used.*/
    public abstract class Instance implements Destroyable {
        /** The size at which this instance was prepared. */
        public final IDimension size;

        /** Returns the background that created this instance. */
        public Background owner () {
            return Background.this;
        }

        /** Adds this background's layers to the specified group at the specified x/y offset.
         * @param depthAdjust an adjustment to the standard depth at which backgrounds are added.
         * This adjustment is added to the standard background depth (-10). This allows one to
         * control the rendering order of multiple backgrounds on a single widget. */
        public abstract void addTo (GroupLayer parent, float x, float y, float depthAdjust);

        /** Disposes of this background instance when it is no longer valid/needed. */
        @Override public abstract void destroy ();

        protected Instance (IDimension size) {
            this.size = new Dimension(size);
        }
    }

    /** The (highest) depth at which background layers are rendered. May range from (-11, 10]. */
    public static final float BACKGROUND_DEPTH = -10f;

    /**
     * Creates a null background (transparent).
     */
    public static Background blank () {
        return new BlankBackground();
    }

    /**
     * Creates a solid background of the specified color.
     */
    public static Background solid (int color) {
        return new SolidBackground(color);
    }

    /**
     * Creates a beveled background with the specified colors.
     */
    public static Background beveled (int bgColor, int ulColor, int brColor) {
        return new BeveledBackground(bgColor, ulColor, brColor);
    }

    /**
     * Creates a bordered background with the specified colors and thickness.
     */
    public static Background bordered (int bgColor, int color, float thickness) {
        return new BorderedBackground(bgColor, color, thickness);
    }

    /**
     * Creates a round rect background with the specified color and corner radius.
     */
    public static Background roundRect (int bgColor, float cornerRadius) {
        return new RoundRectBackground(bgColor, cornerRadius);
    }

    /**
     * Creates a round rect background with the specified colors, border width and corner radius.
     */
    public static Background roundRect (int bgColor, float cornerRadius,
                                        int borderColor, float borderWidth) {
        return new RoundRectBackground(bgColor, cornerRadius, borderColor, borderWidth);
    }

    /**
     * Creates an image background with the specified image.
     */
    public static Background image (Image bgimage) {
        return new ImageBackground(bgimage);
    }

    /**
     * Creates a centered image background with the specified image.
     */
    public static Background centeredImage (Image bgimage) {
        return new CenteredImageBackground(bgimage);
    }

    /**
     * Creates a cropped centered image background with the specified image.
     */
    public static Background croppedImage (Image bgimage) {
        return new CroppedImageBackground(bgimage);
    }

    /**
     * Creates a tiled image background with the specified image.
     */
    public static Background tiledImage (Image bgimage) {
        return new TiledImageBackground(bgimage);
    }

    /**
     * Creates a scale9 background with the specified image. See {@link Scale9Background}.
     */
    public static Scale9Background scale9 (Image scale9Image) {
        return new Scale9Background(scale9Image);
    }

    /**
     * Creates a composite background with the specified backgrounds. See
     * {@link CompositeBackground}.
     */
    public static Background composite (Background... constituents) {
        return new CompositeBackground(constituents);
    }

    /** Instantiates a background at the supplied size. */
    public static Instance instantiate (Background delegate, IDimension size) {
        return delegate.instantiate(size);
    }

    /** The insets of this background, added to get the overall Element size. */
    public Insets insets = Insets.ZERO;

    /** The alpha transparency of this background (or null if no alpha has been configured). */
    public Float alpha;

    /**
     * Configures insets on this background.
     */
    public Background insets (Insets insets) {
        this.insets = insets;
        return this;
    }

    /**
     * Configures uniform insets on this background.
     */
    public Background inset (float uniformInset) {
        insets = Insets.uniform(uniformInset);
        return this;
    }

    /**
     * Configures horizontal and vertical insets on this background.
     */
    public Background inset (float horiz, float vert) {
        insets = Insets.symmetric(horiz, vert);
        return this;
    }

    /**
     * Configures non-uniform insets on this background.
     */
    public Background inset (float top, float right, float bottom, float left) {
        insets = new Insets(top, right, bottom, left);
        return this;
    }

    /**
     * Sets the left inset for this background.
     */
    public Background insetLeft (float left) {
        insets = insets.mutable().left(left);
        return this;
    }

    /**
     * Sets the right inset for this background.
     */
    public Background insetRight (float right) {
        insets = insets.mutable().right(right);
        return this;
    }

    /**
     * Sets the top inset for this background.
     */
    public Background insetTop (float top) {
        insets = insets.mutable().top(top);
        return this;
    }

    /**
     * Sets the bottom inset for this background.
     */
    public Background insetBottom (float bottom) {
        insets = insets.mutable().bottom(bottom);
        return this;
    }

    /**
     * Configures the alpha transparency of this background.
     */
    public Background alpha (float alpha) {
        this.alpha = alpha;
        return this;
    }

    /**
     * Instantiates this background using the supplied widget size. The supplied size should
     * include the insets defined for this background.
     */
    protected abstract Instance instantiate (IDimension size);

    protected Layer createSolidLayer (final int color, final float width, final float height) {
        return graphics().createImmediateLayer(new ImmediateLayer.Renderer() {
            @Override public void render (Surface surf) {
                if (alpha != null) surf.setAlpha(alpha);
                surf.setFillColor(color).fillRect(0, 0, width, height);
                if (alpha != null) surf.setAlpha(1);
            }
        });
    }

    protected Layer createTiledLayer (Image image, float width, float height) {
        image.setRepeat(true, true);
        ImageLayer layer = graphics().createImageLayer(image);
        if (alpha != null) layer.setAlpha(alpha);
        return layer;
    }

    protected class LayerInstance extends Instance {
        public LayerInstance (IDimension size, Layer... layers) {
            super(size);
            _layers = layers;
        }
        public LayerInstance (IDimension size, ImmediateLayer.Renderer renderer) {
            this(size, graphics().createImmediateLayer(renderer));
        }
        @Override public void addTo (GroupLayer parent, float x, float y, float depthAdjust) {
            for (Layer layer : _layers) {
                layer.setDepth(BACKGROUND_DEPTH + depthAdjust);
                layer.transform().translate(x, y); // adjust any existing transform
                parent.add(layer);
            }
        }
        @Override public void destroy () {
            for (Layer layer : _layers) {
                layer.destroy();
            }
        }
        protected Layer[] _layers;
    }
}
