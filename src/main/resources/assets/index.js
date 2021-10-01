function search() {
    let input = document.getElementById("langInput").value;
    let languages = Object.keys(Prism.languages);
    let outElement = document.getElementById("langDropdown");

    outElement.querySelectorAll("div").forEach(x => x.remove());
    if (input === "") {
        for (let lang of ["java", "yaml", "html", "css", "javascript", "json", "python", "csharp"]) {
            outElement.innerHTML += `<div onclick="setLanguage('${lang}')">${lang}</div>`
        }
        outElement.innerHTML += `<div>[search for more]</div>`
        return;
    }
    for (let lang of languages) {
        if (lang.includes(input)) outElement.innerHTML += `<div onclick="setLanguage('${lang}')">${lang}</div>`
    }
}

function endSearch() {
    document.getElementById("langDropdown").classList.add("hidden");
}

function startSearch() {
    document.getElementById("langDropdown").classList.remove("hidden");
    let searchBox = document.getElementById("langInput");
    searchBox.focus();
    searchBox.value = "";
    search();

}

function setLanguage() {
    // todo
}