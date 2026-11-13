window.pageViewsReady.then(loaded => {
  if (!loaded) return;
  const headline = document.querySelector(".hero h1");
  const homeView = document.querySelector(".home-view");
  const introHeading = document.querySelector(".intro-heading h2");
  const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

  const carousel = document.querySelector("[data-step-carousel]");
  const card = carousel && carousel.querySelector(".step-card");

  if (carousel && card && homeView) {
    const slides = [
      {
        number: "01",
        title: "Notice something?",
        copy: "Tell us what’s happening and where. A few details help your report reach the right place.",
        image: "/images/image1.jpg"
      },
      {
        number: "02",
        title: "Send it in",
        copy: "Submit your report in minutes. It will be saved to your account so you can find it again.",
        image: "/images/image2.jpg"
      },
      {
        number: "03",
        title: "Follow the progress",
        copy: "Check your dashboard for status updates as your community report moves forward.",
        image: "/images/image3.jpg"
      }
    ];
    const number = card.querySelector(".step-number");
    const content = card.querySelector(".step-card-content");
    const title = content.querySelector("h3");
    const copy = content.querySelector("p");
    const imageTransitionDuration = prefersReducedMotion ? 0 : 650;
    let activeSlide = 0;
    let timer = null;
    let transitionTimer = null;
    let initialSlideReady = false;

    const preloadedImages = slides.map(slide => {
      const image = new Image();
      image.src = slide.image;
      return image;
    });

    function appendText(element, text, firstLetterIndex) {
      const fragment = document.createDocumentFragment();
      const parts = text.match(/\S+|\s+/g) || [];
      let letterIndex = firstLetterIndex;

      parts.forEach(part => {
        if (/^\s+$/.test(part)) {
          fragment.append(document.createTextNode(part));
          return;
        }

        const word = document.createElement("span");
        word.className = "carousel-word";
        Array.from(part).forEach(character => {
          const letter = document.createElement("span");
          letter.className = "carousel-letter";
          letter.style.setProperty("--letter-index", letterIndex++);
          letter.textContent = character;
          word.append(letter);
        });
        fragment.append(word);
      });

      element.replaceChildren(fragment);
      return letterIndex;
    }

    function renderSlide(slideIndex, animateText) {
      const slide = slides[slideIndex];
      card.dataset.slide = String(slideIndex);
      card.setAttribute("aria-label", `${slideIndex + 1} of ${slides.length}: ${slide.title}`);
      number.textContent = slide.number;
      title.setAttribute("aria-label", slide.title);
      copy.setAttribute("aria-label", slide.copy);

      content.classList.remove("is-revealing");
      if (animateText) {
        const nextLetterIndex = appendText(title, slide.title, 0);
        appendText(copy, slide.copy, nextLetterIndex);
        content.classList.add("is-revealing");
      } else {
        title.textContent = slide.title;
        copy.textContent = slide.copy;
      }
    }

    function scheduleNextSlide() {
      if (initialSlideReady && !homeView.hidden && !document.hidden && timer === null && transitionTimer === null) {
        timer = window.setTimeout(() => {
          timer = null;
          advanceSlide();
        }, 5000);
      }
    }

    function advanceSlide() {
      if (homeView.hidden || document.hidden) return;

      const nextSlide = (activeSlide + 1) % slides.length;
      card.dataset.nextSlide = String(nextSlide);
      card.classList.add("is-transitioning");
      transitionTimer = window.setTimeout(() => {
        activeSlide = nextSlide;
        renderSlide(activeSlide, !prefersReducedMotion);
        delete card.dataset.nextSlide;
        card.classList.remove("is-transitioning");
        transitionTimer = null;
        scheduleNextSlide();
      }, imageTransitionDuration);
    }

    function syncTimer() {
      if (homeView.hidden || document.hidden) {
        window.clearTimeout(timer);
        timer = null;
      } else {
        scheduleNextSlide();
      }
    }

    function showInitialSlide() {
      if (initialSlideReady) return;
      initialSlideReady = true;
      card.classList.remove("is-image-loading");
      renderSlide(activeSlide, !prefersReducedMotion);
      syncTimer();
    }

    card.classList.add("is-image-loading");
    if (preloadedImages[0].complete) {
      requestAnimationFrame(showInitialSlide);
    } else {
      preloadedImages[0].addEventListener("load", showInitialSlide, { once: true });
      preloadedImages[0].addEventListener("error", showInitialSlide, { once: true });
      window.setTimeout(showInitialSlide, 1800);
    }

    window.addEventListener("hashchange", syncTimer);
    document.addEventListener("visibilitychange", syncTimer);
  }

  if (!prefersReducedMotion && "IntersectionObserver" in window && introHeading) {
    introHeading.classList.add("home-scroll-reveal");

    const observer = new IntersectionObserver((entries, currentObserver) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add("is-visible");
          currentObserver.unobserve(entry.target);
        }
      });
    }, { threshold: 0.1, rootMargin: "0px 0px -24px 0px" });

    observer.observe(introHeading);
  }

  if (prefersReducedMotion || !headline || !homeView) return;

  let headlineAnimated = false;

  function animateHeadline() {
    if (headlineAnimated || homeView.hidden) return;

    const walker = document.createTreeWalker(headline, NodeFilter.SHOW_TEXT);
    const textNodes = [];
    while (walker.nextNode()) textNodes.push(walker.currentNode);

    let letterIndex = 0;
    textNodes.forEach(textNode => {
      const fragment = document.createDocumentFragment();
      const parts = textNode.textContent.match(/\S+|\s+/g) || [];

      parts.forEach(part => {
        if (/^\s+$/.test(part)) {
          fragment.append(document.createTextNode(part));
          return;
        }

        const word = document.createElement("span");
        word.className = "headline-word";
        Array.from(part).forEach(character => {
          const letter = document.createElement("span");
          letter.className = "headline-letter";
          letter.setAttribute("aria-hidden", "true");
          letter.style.setProperty("--letter-index", letterIndex++);
          letter.textContent = character;
          word.append(letter);
        });
        fragment.append(word);
      });

      textNode.replaceWith(fragment);
    });

    headline.classList.add("home-headline-typing");
    headlineAnimated = true;
  }

  animateHeadline();
  window.addEventListener("hashchange", animateHeadline);
});