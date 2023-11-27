package com.yrezgui.filepicker.sharedsample

import android.net.Uri

data class Message(val sender: String, val text: String, val attachments: List<Uri> = emptyList())

object SampleData {
    const val contact = "(123) 456-7890"
    const val self = "me"
    val conversationSample = listOf(
        Message("me", "Happy Thanksgiving!"),
        Message("contact", "Thanks man! How's the turkey going on your side?"),
        Message(
            "me",
            "Still in the oven, but hopefully only a few hours more and we're good to go. You?"
        ),
        Message("contact", "I'm dealing with my oven, and it ain't going well -_-"),
        Message("me", "What's wrong? Is it broken?"),
        Message("contact", "Nah, not broken. It's just being stubborn. Temperature issues."),
        Message("me", "Oh no! That sounds frustrating. Have you tried recalibrating it?"),
        Message(
            "contact",
            "Good idea! I'll give it a shot. Meanwhile, your favorite Thanksgiving dish?"
        ),
        Message(
            "me",
            "I'm a sucker for mashed potatoes and gravy. Can't go wrong with that comfort food!"
        ),
        Message(
            "contact",
            "Solid choice! I'm more of a stuffing person myself. The more, the better."
        ),
        Message(
            "me",
            "Agreed! Stuffing is a Thanksgiving essential. Any special recipe you follow?"
        ),
        Message(
            "contact",
            "I have this family recipe with a mix of sausage and cranberries. It's a hit every year!"
        ),
        Message(
            "me",
            "That sounds amazing! Sausage and cranberries add a nice twist. Mind sharing the recipe?"
        ),
        Message(
            "contact",
            "Of course! I'll send it your way. By the way, any fun Thanksgiving traditions on your end?"
        ),
        Message(
            "me",
            "We usually do a 'thankful jar' where everyone writes what they're thankful for. How about you?"
        ),
        Message(
            "contact",
            "Nice tradition! We play touch football in the backyard. It gets pretty competitive!"
        )
    )
}
