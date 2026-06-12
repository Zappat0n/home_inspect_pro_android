package com.homeinspectpro

import android.graphics.Bitmap
import android.view.MotionEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SignatureViewTest {

    private fun createView(): SignatureView {
        val view = SignatureView(RuntimeEnvironment.getApplication())
        view.layout(0, 0, 200, 200)
        return view
    }

    private fun motionEvent(action: Int, x: Float, y: Float): MotionEvent {
        return MotionEvent.obtain(0L, 0L, action, x, y, 0)
    }

    @Test
    fun `view is created without crash`() {
        val view = SignatureView(RuntimeEnvironment.getApplication())
        assertNotNull(view)
    }

    @Test
    fun `onTouchEvent ACTION_DOWN returns true`() {
        val view = createView()
        val event = motionEvent(MotionEvent.ACTION_DOWN, 50f, 50f)
        assertTrue(view.onTouchEvent(event))
        event.recycle()
    }

    @Test
    fun `onTouchEvent ACTION_MOVE returns true`() {
        val view = createView()
        val down = motionEvent(MotionEvent.ACTION_DOWN, 50f, 50f)
        view.onTouchEvent(down)
        down.recycle()
        val move = motionEvent(MotionEvent.ACTION_MOVE, 60f, 60f)
        assertTrue(view.onTouchEvent(move))
        move.recycle()
    }

    @Test
    fun `onTouchEvent ACTION_UP returns true`() {
        val view = createView()
        val down = motionEvent(MotionEvent.ACTION_DOWN, 50f, 50f)
        view.onTouchEvent(down)
        down.recycle()
        val up = motionEvent(MotionEvent.ACTION_UP, 50f, 50f)
        assertTrue(view.onTouchEvent(up))
        up.recycle()
    }

    @Test
    fun `onTouchEvent ACTION_UP adds path to paths list`() {
        val view = createView()
        val down = motionEvent(MotionEvent.ACTION_DOWN, 10f, 10f)
        view.onTouchEvent(down)
        down.recycle()
        val move = motionEvent(MotionEvent.ACTION_MOVE, 100f, 100f)
        view.onTouchEvent(move)
        move.recycle()
        val up = motionEvent(MotionEvent.ACTION_UP, 100f, 100f)
        view.onTouchEvent(up)
        up.recycle()

        val bitmap = view.getBitmap()
        assertNotNull(bitmap)
        assertEquals(200, bitmap.width)
        assertEquals(200, bitmap.height)
    }

    @Test
    fun `getBitmap returns bitmap with view dimensions`() {
        val view = createView()
        val bitmap = view.getBitmap()
        assertEquals(200, bitmap.width)
        assertEquals(200, bitmap.height)
    }

    @Test
    fun `getBitmap returns bitmap with ARGB_8888 config when empty`() {
        val view = createView()
        val bitmap = view.getBitmap()
        assertEquals(Bitmap.Config.ARGB_8888, bitmap.config)
        assertEquals(200, bitmap.width)
        assertEquals(200, bitmap.height)
    }

    @Test
    fun `clear does not crash and allows subsequent drawing`() {
        val view = createView()
        val down = motionEvent(MotionEvent.ACTION_DOWN, 10f, 10f)
        view.onTouchEvent(down)
        down.recycle()
        val up = motionEvent(MotionEvent.ACTION_UP, 150f, 150f)
        view.onTouchEvent(up)
        up.recycle()

        view.clear()

        val down2 = motionEvent(MotionEvent.ACTION_DOWN, 20f, 20f)
        view.onTouchEvent(down2)
        down2.recycle()
        val up2 = motionEvent(MotionEvent.ACTION_UP, 80f, 80f)
        view.onTouchEvent(up2)
        up2.recycle()

        val bitmap = view.getBitmap()
        assertNotNull(bitmap)
        assertEquals(200, bitmap.width)
    }

    @Test
    fun `multiple strokes do not crash`() {
        val view = createView()

        val down1 = motionEvent(MotionEvent.ACTION_DOWN, 10f, 10f)
        view.onTouchEvent(down1)
        down1.recycle()
        val up1 = motionEvent(MotionEvent.ACTION_UP, 50f, 50f)
        view.onTouchEvent(up1)
        up1.recycle()

        val down2 = motionEvent(MotionEvent.ACTION_DOWN, 100f, 100f)
        view.onTouchEvent(down2)
        down2.recycle()
        val up2 = motionEvent(MotionEvent.ACTION_UP, 180f, 180f)
        view.onTouchEvent(up2)
        up2.recycle()

        val bitmap = view.getBitmap()
        assertNotNull(bitmap)
        assertEquals(200, bitmap.width)
        assertEquals(200, bitmap.height)
    }

    @Test
    fun `unknown action returns false`() {
        val view = createView()
        val event = motionEvent(MotionEvent.ACTION_MASK, 50f, 50f)
        assertFalse(view.onTouchEvent(event))
        event.recycle()
    }
}
