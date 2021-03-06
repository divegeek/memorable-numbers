First, credit where credit is due: This little project was sparked by
this XKCD comic on password security (https://xkcd.com/936/):

![https://xkcd.com/936/](https://imgs.xkcd.com/comics/password_strength.png)

The comic is about the amount of entropy contained in a passphrase of
a few short, common, memorable words.  In the comic, the idea is to
use these words in place of a shorter, but more complicated and less
memorable password.  It occurred to me, though, that I manage lots of
long numbers, and that's hard to do.

At work, there are bug ID numbers, changelist numbers, user ID
numbers, etc.  These are all just long enough that they're not only
not memorizable, they don't even stick in short-term memory well
enough to glance at one, switch to a different tab or window, and
retype it.  So I copy and paste a lot, which is fine, but sometimes
the numbers are (helpfully!) hyperlinks, which makes selecting them
for copying without clicking on them hard.

In the rest of my life, there are lots of phone numbers.  In practice
I keep them all in my phone so I never have to know what they are, but
if I don't have my phone, I'm sunk, outside of a very small set of
numbers that I keep memorized.

So, what if we define a mapping from a dictionary of common words onto
numbers, and then use that to build a nice number to word-sequence
translation tool?  If the translation were built into all the
appropriate places, we could just use either numbers or words,
whichever is convenient.

For example, where I work we have a web interface to the change
management system.  If that web interface displayed a sequence of
three simple words next to each change number, it would be really easy
to shout a "number" over the cubicle wall and have it understood and
easily typed.  In experimenting with it, I even found that although
it's more keystrokes I can type a sequence of simple words faster
than a number.

Then I started playing with phone numbers and I found that I can map
phone numbers onto a three-word sequence, making them really easy to
remember.  For example, my old home phone number translates as "calm
restore utterly".  I find that far easier to remember than
801-479-0406 (note that I don't know if someone has that number; don't
call and bother them, please, you won't get me!).  Of course, if you
live in Utah you don't really have to "remember" 801 -- because nearly
all of the numbers are 801... but the same holds true here.  Pretty
much all 801 numbers translate to strings with "calm" as the first
word.  So someone would really only have to remember "restore utterly"
to know my number.

A mobile phone interface using this could use the standard type-ahead
features, but restricted to the known dictionary, so you could
probably type my number with "ca re ut", tapping each full word when
it pops up.

So, this is a Java implementation of the concept, complete with two
dictionaries, "large" and "small".  Large has 4096 words so each word
represents 12 bits.  This allows all US phone numbers to be
represented with three words, but to get 4096 common words, I had to
reach into slightly longer (up to 8 letters) and less well-known
words.

The small dictionary contains 1626 words.  This number was chosen so
that three words can represent the full range of a 32-bit integer.
Some phone numbers require four words with this, but if you're working
with slightly smaller numbers, it's great because the words are
shorter and more common.

In both cases I tried to weed out homonyms and words which are often
hard to spell.  I also excluded offensive words.  I'm sure more can be
done to improve these dictionaries, but they're pretty usable.

Note that I also looked at the diceware list, and at Beale's
alternative list, and discarded them. There may be some useful ideas
that can be gleaned by studying the lists, but the difference between
those lists and my large list is mostly "words" that are either very
obscure or else not really words at all. There are lots of numbers and
symbols. Those are good for passwords, but not very memorable or easy
to exchange verbally. And the additional 3680 words only add 0.925
bits of entropy per word used, so they don't really help all that
much.

However, I have added them in case someone wants to try them.
