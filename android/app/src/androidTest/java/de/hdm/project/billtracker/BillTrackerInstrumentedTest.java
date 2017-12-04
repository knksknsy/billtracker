package de.hdm.project.billtracker;

import android.os.Build;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.hdm.project.billtracker.activities.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 21)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class BillTrackerInstrumentedTest {
    // Globals
    private static final String TEST_MAIL = "billtracker2017@gmail.com";
    private static final String TEST_PASS = "hdmstuttgart";
    private static final String TEST_CATEGORY = "Foodtest";
    private static final String TEST_ITEM = "Bananas";
    private static final String TEST_ITEM_CHANGE = "Apples";
    private static final String TEST_SUM = "1.50";
    private static final String LOG_TAG = "LogWatcher Just4Testing";

    private MainActivity mainActivity;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void launchActivity() throws Exception {
        mainActivity = mainActivityRule.getActivity();
    }

    @Before
    public void login() throws Exception {
        try {
            onView(withId(R.id.input_email)).perform(clearText(),typeText(TEST_MAIL), closeSoftKeyboard());
            onView(withId(R.id.input_password)).perform(clearText(),typeText(TEST_PASS), closeSoftKeyboard());
            onView(withId(R.id.btn_login)).perform(click());
        } catch (NoMatchingViewException e) {
            Log.w(LOG_TAG, "Already logged in!");
            e.printStackTrace();
        }
    }

    @Test
    public void t1_takeAndSaveBill() throws Exception {
        // Grant permission
        Thread.sleep(500);
        allowDevicePermission();
        Thread.sleep(500);
        allowDevicePermission();
        // Type sum of the bill
        onView(withId(R.id.totalSum)).perform(clearText(),typeText(TEST_SUM), closeSoftKeyboard());
        // Take photo
        onView(withId(R.id.photoButton)).perform(click());
        Thread.sleep(2000);
        // Save bill
        onView(withId(R.id.saveButton)).perform(click());
        // Type Category
        onView(withId(R.id.autocompleteCategory)).perform(clearText(),typeText(TEST_CATEGORY), closeSoftKeyboard());
        // Check if Category already exist
        onView(withId(R.id.autocompleteCategory)).check(matches(withText(TEST_CATEGORY))).perform(click());
        // Type bill title
        onView(withId(R.id.titleText)).perform(clearText(),typeText(TEST_ITEM), closeSoftKeyboard());
        // Upload bill to Firebase
        onView(withText("Save")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        // Cancel bill creation
        //onView(withText("Cancel")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void t2_checkIfBillExist() throws Exception {
        // Switch to Category-Fragment
        Thread.sleep(1000);
        onView(allOf(withText("Bills"),
                isDescendantOfA(withId(R.id.navigation)), isDisplayed())).perform(click());
        onView(withText(TEST_CATEGORY.toUpperCase())).perform(click());
        onView(withText(TEST_ITEM)).perform(click());
    }

    @Test
    public void t3_re_editBill() throws Exception {
        // Go to testBill
        onView(allOf(withText("Bills"),
                isDescendantOfA(withId(R.id.navigation)), isDisplayed())).perform(click());
        onView(withText(TEST_CATEGORY.toUpperCase())).perform(click());
        onView(withText(TEST_ITEM)).perform(click());
        // Change Itemname
        onView(withText(TEST_ITEM)).perform(replaceText(TEST_ITEM_CHANGE));
        onView(withId(R.id.saveButton)).perform(click());
        Thread.sleep(2000);
        // Undo change
        onView(withText(TEST_ITEM_CHANGE)).perform(click());
        onView(withText(TEST_ITEM_CHANGE)).perform(replaceText(TEST_ITEM));
        onView(withId(R.id.saveButton)).perform(click());
    }

    @Test
    public void t4_deleteBill() throws Exception {
        // Go to testBill
        onView(allOf(withText("Bills"),
                isDescendantOfA(withId(R.id.navigation)), isDisplayed())).perform(click());
        onView(withText(TEST_CATEGORY.toUpperCase())).perform(click());
        onView(withText(TEST_ITEM)).perform(click());
        // Delete testBill
        onView(withId(R.id.deleteButton)).perform(click());
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        // Delete testCategory
        //onView(withId(R.id.deleteButton)).perform(click());
        //onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        //onView(isRoot()).perform(ViewActions.pressBack());
    }

    private static void allowDevicePermission() throws Exception {
        if (Build.VERSION.SDK_INT >= 21) {
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector().text("ALLOW"));
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click();
                } catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}