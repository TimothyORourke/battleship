var isSetup = true;
var placedShips = 0;
var game;
var shipType;
var vertical;
var darkMode = false;
var shipRemain;

function makeGrid(table, isPlayer, darkMode) {
    for (i=0; i<10; i++) {
        let row = document.createElement('tr');
        if (darkMode) {
            row.style.borderColor = "#797b7f";
        }

        for (j=0; j<10; j++) {
            let column = document.createElement('td');
            column.addEventListener("click", cellClick);
            if (darkMode) {
                column.style.borderColor = "#797b7f";
            }
            row.appendChild(column);
        }
        table.appendChild(row);
    }
}

function markHits(board, elementId, surrenderText) {
    board.attacks.forEach((attack) => {
        let className;
        if (attack.result === "MISS"){
            className = "miss";
        }
        else if (attack.result === "HIT"){
            className = "hit";
        }
        else if (attack.result === "SUNK"){
            className = "sink"
        }
        else if (attack.result === "HITTOSUNK"){
            className = "sink"
        }
        else if (attack.result === "SURRENDER"){
            className = "sink"
        }
        else if (attack.result === "INVALID"){
        }
        document.getElementById(elementId).rows[attack.location.row-1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add(className);
    });
}

function sendMessage(str){
    document.getElementById("message").innerHTML = str;
}

function updateStatusBar(){
    //Checking last result from attacks on opponent's board
    switch(game.opponentsBoard.attacks[game.opponentsBoard.attacks.length - 1].result){
        case "MISS":
            sendMessage("That's a miss!");
            break;
        case "HIT":
            sendMessage("Direct hit!")
            break;
        case "SUNK":
            sendMessage("You sent that ship to Davey Jones' locker!");
            break;
        case "SURRENDER":
            sendMessage("You won, sir!");
            break;
        case "INVALID":
            sendMessagge("We cant find that coordinate, try again!");
            break;
        case "DUPLICATE":
            sendMessage("You've already attacked there, try again!");
            break;
        default:
            sendMessage("Captain has scurvy again, he's speaking nonsense!");
    }
}

function updateRemain(board) {
    shipRemain = 3; 
    // only checks
    board.attacks.forEach((attack) => {
        if (attack.result === "SUNK" || attack.result === "SURRENDER") {
            shipRemain--; 
        }
    });
    document.getElementById("numShipDisplay").innerHTML = shipRemain;
}

function redrawGrid() {
    Array.from(document.getElementById("opponent").childNodes).forEach((row) => row.remove());
    Array.from(document.getElementById("player").childNodes).forEach((row) => row.remove());
    makeGrid(document.getElementById("opponent"), false, darkMode);
    makeGrid(document.getElementById("player"), true, darkMode);
    if (game === undefined) {
        return;
    }

    game.playersBoard.ships.forEach((ship) => ship.occupiedSquares.forEach((square) => {
        document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("occupied");
    }));
    markHits(game.opponentsBoard, "opponent", "You won the game");
    markHits(game.playersBoard, "player", "You lost the game");
    updateRemain(game.opponentsBoard);
}

var oldListener;
function registerCellListener(f) {
    let el = document.getElementById("player");
    for (i=0; i<10; i++) {
        for (j=0; j<10; j++) {
            let cell = el.rows[i].cells[j];
            cell.removeEventListener("mouseover", oldListener);
            cell.removeEventListener("mouseout", oldListener);
            cell.addEventListener("mouseover", f);
            cell.addEventListener("mouseout", f);
        }
    }
    oldListener = f;
}

function cellClick() {
    let row = this.parentNode.rowIndex + 1;
    let col = String.fromCharCode(this.cellIndex + 65);
    console.log(col);
    if (isSetup) {
        sendXhr("POST", "/place", {game: game, shipType: shipType, x: row, y: col, isVertical: vertical}, function(data) {
            game = data;

            redrawGrid();
            placedShips++;

            if (placedShips == 1) {
                shipType = "DESTROYER";
                createPlacementShadow(shipType);
            }

            else if (placedShips == 2) {
                shipType = "BATTLESHIP";
                createPlacementShadow(shipType);
            }

            else if (placedShips == 3) {
                isSetup = false;
                registerCellListener((e) => {});
                sendMessage("Choose a location to attack, sir!")

            }
        });
    } else {
        sendXhr("POST", "/attack", {game: game, x: row, y: col}, function(data) {
            game = data;
            updateStatusBar();
            redrawGrid();
        })
    }
}

function sendXhr(method, url, data, handler) {
    var req = new XMLHttpRequest();
    req.addEventListener("load", function(event) {
        if (req.status != 200) {
            sendMessage("You can't do that, sir!");
            document.getElementById("status_bar").classList.add("error");
            return;
        }
        sendMessage("Place your fleet, captain!");
        document.getElementById("status_bar").classList.remove("error");
        handler(JSON.parse(req.responseText));
    });
    req.open(method, url);
    req.setRequestHeader("Content-Type", "application/json");
    req.send(JSON.stringify(data));
}

function place(size) {
    return function() {
        let row = this.parentNode.rowIndex;
        let col = this.cellIndex;
        vertical = document.getElementById("is_vertical").checked;
        let table = document.getElementById("player");
        for (let i=0; i<size; i++) {
            let cell;
            if(vertical) {
                let tableRow = table.rows[row+i];
                if (tableRow === undefined) {
                    // ship is over the edge; let the back end deal with it
                    break;
                }
                cell = tableRow.cells[col];
            } else {
                cell = table.rows[row].cells[col+i];
            }
            if (cell === undefined) {
                // ship is over the edge; let the back end deal with it
                break;
            }
            cell.classList.toggle("placed");
        }
    }
}

function createPlacementShadow(shipType){
    switch(shipType){
        case "MINESWEEPER":
            registerCellListener(place(2));
            break;
        case "DESTROYER":
            registerCellListener(place(3));
            break;
        case "BATTLESHIP":
            registerCellListener(place(4));
            break;
        default:
            registerCellListener(place(1));
        }
}

function initGame() {
    makeGrid(document.getElementById("opponent"), false, darkMode);
    makeGrid(document.getElementById("player"), true, darkMode);
    sendMessage("Place your fleet, captain!");
    shipType = "MINESWEEPER";
    createPlacementShadow(shipType);

    document.getElementById("dark_mode").addEventListener("click", function(e) {
        if (darkMode) {
            document.body.style.backgroundColor = "white";
            document.body.style.color = "black";
            document.getElementById("opponent").style.borderColor = "black";
            document.getElementById("player").style.borderColor = "black";
            darkMode = false;
        }
        else {
            document.body.style.backgroundColor = "black";
            document.body.style.color = "white";
            document.getElementById("opponent").style.borderColor = "#797b7f";
            document.getElementById("player").style.borderColor = "#797b7f";
            darkMode = true;
        }
        redrawGrid();
        createPlacementShadow(shipType);
    });
    document.getElementById("rules_button").addEventListener("click", function(e) {
        document.getElementById("rules").classList.toggle("hidden");
        document.getElementById("color_codes").classList.toggle("hidden");
    });

    document.getElementById("new_game").addEventListener("click", function(e) {
        sendXhr("GET", "/game", {}, function(data) {
            game = data;
            isSetup = true;
            placedShips = 0;
            redrawGrid();
            shipType = "MINESWEEPER";
            createPlacementShadow(shipType);
        });
    });

    document.addEventListener('contextmenu', function(e) {
        e.preventDefault();
        if (document.getElementById("is_vertical").checked === true){
            document.getElementById("is_vertical").checked = false;
        }
        else if (document.getElementById("is_vertical").checked === false){
            document.getElementById("is_vertical").checked = true;
        }
        redrawGrid();
        createPlacementShadow(shipType);
        return false;
    });
  
    sendXhr("GET", "/game", {}, function(data) {
        game = data;
    });
};
