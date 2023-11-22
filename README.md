
# Vertici e Vertigini con Vert.x
VVV mi piaci tu event-loop

---

```java
record EventLoop(Deque<String> events, Map<String, Consumer<String>> handlers) {
  EventLoop() { this(new ConcurrentLinkedDeque<>(), new ConcurrentHashMap<>()); }

  void dispatch(String event) { events.add(event); }
  void stop() {Thread.current}
}

void  main() {
  var eventLoop = new EventLoop();
}
```

---

## Everything is markdown
In fact this entire presentation is a markdown file

---

# h1
## h2
### h3
#### h4
##### h5
###### h6

# Markdown components
You can use everything in markdown!
* Like bulleted list
* You know the deal

1. Numbered lists too

| Tables | Too    |
| ------ | ------ |
| Even   | Tables |

---

All you need to do is separate slides with triple dashes `---` on a separate line,
like so:

```markdown
# Slide 1
Some stuff

--- 

# Slide 2
Some other stuff
```
