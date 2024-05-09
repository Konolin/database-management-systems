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
        setOutputText("1");
    }

    const handleDirtyWrite = () => {
        setExplanationText(DIRTY_WRITE_EXPLANATION);
        fetch("http://localhost:8080/api/concurrency-issues-java/dirty-write?id=1", {
            method: 'POST'
        })
            .then((response) => response.json())
            .then(data => setOutputText(data));
    }

    const handleLostUpdate = () => {
        setExplanationText(LOST_UPDATE_EXPLANATION);
        setOutputText("3");
    }

    const handlePhantomRead = () => {
        setExplanationText(PHANTOM_READ_EXPLANATION);
        setOutputText("4");
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
