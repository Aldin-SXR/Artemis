import { MonacoCodeEditorElement } from 'app/shared/monaco-editor/model/monaco-code-editor-element.model';
import * as monaco from 'monaco-editor';

type GlyphMarginLane = monaco.editor.GlyphMarginLane;

/**
 * Class representing a glyph margin widget in the Monaco editor.
 * Glyph margin widgets refer to one line and can contain arbitrary DOM nodes.
 */
export class MonacoEditorGlyphMarginWidget extends MonacoCodeEditorElement implements monaco.editor.IGlyphMarginWidget {
    private readonly domNode: HTMLElement;
    private lineNumber: number;
    private readonly lane: GlyphMarginLane;

    /**
     * @param editor The editor to render this widget in.
     * @param id The id of this widget.
     * @param domNode The DOM node to render in the glyph margin.
     * @param lineNumber The line to render this widget in.
     * @param lane The lane (left, center, or right) to render the widget in.
     */
    constructor(editor: monaco.editor.ICodeEditor, id: string, domNode: HTMLElement, lineNumber: number, lane: GlyphMarginLane) {
        super(editor, id);
        this.domNode = domNode;
        this.lineNumber = lineNumber;
        this.lane = lane;
    }

    getDomNode(): HTMLElement {
        return this.domNode;
    }
    getPosition(): monaco.editor.IGlyphMarginWidgetPosition {
        return {
            lane: this.lane,
            zIndex: 10,
            range: new monaco.Range(this.lineNumber, 0, this.lineNumber, 0),
        };
    }

    getLineNumber(): number {
        return this.lineNumber;
    }

    setLineNumber(lineNumber: number): void {
        this.lineNumber = lineNumber;
    }

    addToEditor(): void {
        this.editor.addGlyphMarginWidget(this);
    }
    removeFromEditor(): void {
        this.editor.removeGlyphMarginWidget(this);
    }
}
