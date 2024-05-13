import './App.css';
import {useState} from "react";
import {
    DIRTY_READ_EXPLANATION,
    DIRTY_WRITE_EXPLANATION,
    LOST_UPDATE_EXPLANATION,
    PHANTOM_READ_EXPLANATION, UNREPEATABLE_READ_EXPLANATION
} from "./explanations";

export default function App() {
    const [outputText, setOutputText] = useState("");
    const [explanationText, setExplanationText] = useState("");

    const handleDirtyRead = () => {
        setExplanationText(DIRTY_READ_EXPLANATION);
        fetch("http://localhost:8080/api/concurrency-issues-java/dirty-read?id=1", {
            method: 'POST'
        })
            .then((response) => response.json())
            .then(data => {
                const {startingFollowers, modifiedFollowersTransaction1, modifiedFollowersTransaction2, rollbackTransaction1, finalFollowers} = data;
                const text =
                    `The starting name: ${startingFollowers}\nThe name after transaction 1: ${modifiedFollowersTransaction1}\nThe name after transaction 2: ${modifiedFollowersTransaction2}\nRollback transaction 1: ${rollbackTransaction1}\nThe final name: ${finalFollowers}`;
                setOutputText(text);
            });
    }

    const handleDirtyWrite = () => {
        setExplanationText(DIRTY_WRITE_EXPLANATION);
        fetch("http://localhost:8080/api/concurrency-issues-java/dirty-write?id=1", {
            method: 'POST'
        })
            .then((response) => response.json())
            .then(data => {
                const {startingName, modifiedNameTransaction1, modifiedNameTransaction2, finalName} = data;
                const text =
                    `The starting name: ${startingName}\nThe name after transaction 1: ${modifiedNameTransaction1}\nThe name after transaction 2: ${modifiedNameTransaction2}\nThe final name: ${finalName}`;
                setOutputText(text);
            });
    }

    const handleLostUpdate = () => {
        setExplanationText(LOST_UPDATE_EXPLANATION);
        setOutputText("3");
    }

    const handlePhantomRead = () => {
        setExplanationText(PHANTOM_READ_EXPLANATION);
        fetch("http://localhost:8080/api/concurrency-issues-java/phantom-read", {
            method: 'POST'
        })
            .then((response) => response.json())
            .then(data => {
                console.log(data);
                const {firstRowCount, secondRowCount} = data;
                const text =
                    `First row count: ${firstRowCount}\nSecond row count: ${secondRowCount}`;
                setOutputText(text);
            });
    }

    const handleUnrepeatableRead = () => {
        setExplanationText(UNREPEATABLE_READ_EXPLANATION);
        setOutputText("5");
    }

    return (
        <>
            <nav className="navbar">
                <h1>Concurrency issues</h1>
            </nav>
            <div className="container">
                <div className="button-container">
                    <button onClick={handleDirtyRead}>Dirty Read</button>
                    <button onClick={handleDirtyWrite}>Dirty Write</button>
                    <button onClick={handleLostUpdate}>Lost Update</button>
                    <button onClick={handlePhantomRead}>Phantom Read</button>
                    <button onClick={handleUnrepeatableRead}>Unrepeatable Read</button>
                </div>
                <div className="output-container">
                    <textarea className="textarea explanation" readOnly value={explanationText}/>
                    <textarea className="textarea  output" readOnly value={outputText}/>
                </div>
            </div>
        </>
    );
}
